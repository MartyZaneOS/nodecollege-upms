package com.nodecollege.cloud.service.impl;

import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.constants.RedisConstants;
import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.po.OperateAdmin;
import com.nodecollege.cloud.common.model.po.OperateAdminPassword;
import com.nodecollege.cloud.common.model.po.OperateUser;
import com.nodecollege.cloud.common.model.po.OperateUserPassword;
import com.nodecollege.cloud.common.utils.PasswordUtil;
import com.nodecollege.cloud.common.utils.RedisUtils;
import com.nodecollege.cloud.cache.ConfigCache;
import com.nodecollege.cloud.dao.mapper.OperateAdminPasswordMapper;
import com.nodecollege.cloud.dao.mapper.OperateUserPasswordMapper;
import com.nodecollege.cloud.service.PasswordPolicyService;
import com.nodecollege.cloud.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author LC
 * @date 2019/12/1 13:25
 */
@Service
public class PasswordPolicyServiceImpl implements PasswordPolicyService {

    @Autowired
    private UserService userService;

    @Autowired
    private OperateAdminPasswordMapper operateAdminPasswordMapper;

    @Autowired
    private OperateUserPasswordMapper operateUserPasswordMapper;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 验证密码策略
     *
     * @param userName    用户名称
     * @param password    明文用户密码
     * @param newPassword 明文新用户密码
     * @param cert        验证证书
     */
    @Override
    public void checkLoginPolicy(String userName, String password, String newPassword, String cert) {
        // 查询登陆失败次数
        Integer loginFileNum = redisUtils.get(RedisConstants.ADMIN_LOGIN_FAIL_NUM + userName, Integer.class);
        loginFileNum = loginFileNum == null ? 0 : loginFileNum;
        // 1 登陆失败{lockThreshold}次，锁定用户 0-不锁定，10-十次（默认）
        if (loginFileNum > ConfigCache.LOCK_THRESHOLD) {
            throw new NCException("-1", MessageFormat.format("登陆失败超过{0}次，账号被锁定！", ConfigCache.LOCK_THRESHOLD));
        }

        // 2 登陆失败{checkThreshold}次，验证验证码 0-验证码必填，3-三次（默认）
        if (loginFileNum > ConfigCache.CHECK_THRESHOLD && StringUtils.isBlank(cert)) {
            throw new NCException("-1", "请输入验证码！");
        }
    }

    /**
     * 验证密码策略
     *
     * @param userName    用户名称
     * @param password    明文用户密码
     * @param newPassword 明文新用户密码
     * @param cert        验证证书
     */
    @Override
    public void checkUserLoginPolicy(String userName, String password, String newPassword, String cert) {
        // 查询登陆失败次数
        Integer loginFileNum = redisUtils.get(RedisConstants.USER_LOGIN_FAIL_NUM + userName, Integer.class);
        loginFileNum = loginFileNum == null ? 0 : loginFileNum;
        // 1 登陆失败{lockThreshold}次，锁定用户 0-不锁定，10-十次（默认）
        if (loginFileNum > ConfigCache.LOCK_THRESHOLD) {
            throw new NCException("-1", MessageFormat.format("登陆失败超过{0}次，账号被锁定！", ConfigCache.LOCK_THRESHOLD));
        }

        // 2 登陆失败{checkThreshold}次，验证验证码 0-验证码必填，3-三次（默认）
        if (loginFileNum > ConfigCache.CHECK_THRESHOLD && StringUtils.isBlank(cert)) {
            throw new NCException("-1", "请输入验证码！");
        }
    }

    /**
     * 登陆成功后校验密码策略
     *
     * @param admin
     * @param passwordList
     * @param newPassword
     * @param cert
     */
    @Override
    public void checkLoginSuccessPolicy(OperateAdmin admin, List<OperateAdminPassword> passwordList, String newPassword, String cert) {
        // 3 首次登陆{firstLoginCheck} 修改密码 true-需要（默认），false-不需要
        if (ConfigCache.FIRST_LOGIN_CHECK && admin.getFirstLogin() && (StringUtils.isBlank(newPassword) || StringUtils.isBlank(cert))) {
            throw new NCException("10001", "首次登陆需要修改密码，请输入新密码和验证码！");
        }

        // 4 密码有效期超过{passwordValidity}天，重新设置密码 0-不限制（默认），验证用户名、旧密码、新密码、图片验证码正确后登陆成功
        if (0 > ConfigCache.PASSWORD_VALIDITY) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE, -ConfigCache.PASSWORD_VALIDITY);
            // 当用户密码时间超过指定天数时，需要校验新密码和图片验证码
            if (passwordList.get(0).getCreateTime().compareTo(calendar.getTime()) < 0) {
                if (StringUtils.isBlank(newPassword) || StringUtils.isBlank(cert)) {
                    throw new NCException("-1", "密码过期，请设置新密码和校验码！");
                }
                // 4.1 校验注册密码策略
                checkRegisterPolicy(newPassword, admin.getAccount());
                // 4.2 校验更新用户密码策略
                checkUpdatePolicy(passwordList, newPassword);
            }
        }
    }


    /**
     * 登陆成功后校验密码策略
     *
     * @param userId
     * @param firstLogin
     */
    @Override
    public Boolean checkUserLoginSuccessPolicy(Long userId, Integer firstLogin) {
        // 3 首次登陆{firstLoginCheck} 修改密码 true-需要（默认），false-不需要
        if (ConfigCache.FIRST_LOGIN_CHECK && firstLogin == 0) {
            throw new NCException("10001", "首次登陆需要修改密码，请输入新密码和验证码！");
        }
        // 4 密码有效期超过{passwordValidity}天，重新设置密码 0-不限制（默认），验证用户名、旧密码、新密码、图片验证码正确后登陆成功
        if (0 < ConfigCache.PASSWORD_VALIDITY) {
            List<OperateUserPassword> passwordList = operateUserPasswordMapper.selectListByUserId(userId);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE, -ConfigCache.PASSWORD_VALIDITY);
            if (passwordList == null || passwordList.isEmpty()) {
                OperateUser operateUser = userService.getUserByUserId(userId);
                if (operateUser.getCreateTime().compareTo(calendar.getTime()) < 0) {
                    return false;
                }
            }
            // 当用户密码时间超过指定天数时，需要校验新密码和图片验证码
            if (passwordList.get(0).getCreateTime().compareTo(calendar.getTime()) < 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 校验注册密码策略
     *
     * @param password 明文用户密码
     * @param account  用户名称/电话
     */
    @Override
    public void checkRegisterPolicy(String password, String account) {

        // 0 密码与账号相同验证{pwdEqualUserCheck} true-需要，false-不需要（默认）
        if (ConfigCache.PWD_EQUAL_USER_CHECK && password.equalsIgnoreCase(account)) {
            throw new NCException("-1", "密码不能与账号相同！");
        }

        // 1 最小密码长度{passwordLength}位 6-六位（最小） 8-八位（默认）
        if (ConfigCache.PASSWORD_LENGTH > password.length() || NCConstants.INT_6 > password.length()) {
            String message = MessageFormat.format("密码长度不能小于{0}位有效长度", ConfigCache.PASSWORD_LENGTH > 6 ? ConfigCache.PASSWORD_LENGTH : 6);
            throw new NCException("-1", message);
        }
        // 2 密码组成规则 包含以下4种字符类型的任意种组合
        // 2.1 大写英文字母: A-Z {containCapital}   0-不限制（默认），1-至少包含一位
        if (0 < ConfigCache.CONTAIN_CAPITAL_ENGLISH) {
            if (!password.matches("^.*[A-Z]{" + ConfigCache.CONTAIN_CAPITAL_ENGLISH + ",}.*")) {
                String message = MessageFormat.format("密码中至少包含{0}位大写英文字母", ConfigCache.CONTAIN_CAPITAL_ENGLISH);
                throw new NCException("-1", message);
            }
        }
        // 2.2 小写英文字母: a-z {containLowercase} 0-不限制（默认），1-至少包含一位
        if (0 < ConfigCache.CONTAIN_LOWERCASE_ENGLISH) {
            if (!password.matches("^.*[a-z]{" + ConfigCache.CONTAIN_LOWERCASE_ENGLISH + ",}.*")) {
                String message = MessageFormat.format("密码中至少包含{0}位小写英文字母", ConfigCache.CONTAIN_LOWERCASE_ENGLISH);
                throw new NCException("-1", message);
            }
        }
        // 2.3 阿拉伯数字: 0-9   {containNumber}    1-至少包含一位（默认）
        if (0 < ConfigCache.CONTAIN_NUMBER) {
            if (!password.matches("^.*[0-9]{" + ConfigCache.CONTAIN_NUMBER + ",}.*")) {
                String message = MessageFormat.format("密码中至少包含{0}位阿拉伯数字", ConfigCache.CONTAIN_NUMBER);
                throw new NCException("-1", message);
            }
        }
        // 2.4 特殊字符: `~-_+=[]{}\|;:'",<.>/?!@#$%^&*()  {containSpecial} 0-不限制（默认），1-至少包含一位
        if (0 < ConfigCache.CONTAIN_SPECIAL) {
            if (!password.matches("^.*[\\W]{" + ConfigCache.CONTAIN_SPECIAL + ",}.*")) {
                String message = MessageFormat.format("密码中至少包含{0}位特殊字符", ConfigCache.CONTAIN_SPECIAL);
                throw new NCException("-1", message);
            }
        }
        // 2.5 首字母不允许使用特殊字符
        if (password.matches("^[\\W]{1,}.*")) {
            throw new NCException("-1", "首字母不允许使用特殊字符");
        }
    }

    /**
     * 校验更新用户密码策略
     *
     * @param list        历史密码信息
     * @param newPassword 明文新密码
     */
    @Override
    public void checkUpdatePolicy(List<OperateAdminPassword> list, String newPassword) {
        // 1 新密码不能和现在使用的密码一样
        if (list.get(0).getPassword().equals(PasswordUtil.md5(newPassword, list.get(0).getSalt()))) {
            throw new NCException("-1", "新密码不能和现在使用的密码一样！");
        }
        // 2 最近{historySize}次历史密码重复校验限制 0-不限制（默认），5-最近5次密码不能相同
        if (0 < ConfigCache.HISTORY_SIZE) {
            for (int i = 0; i < list.size(); i++) {
                if (i < ConfigCache.HISTORY_SIZE) {
                    if (list.get(i).getPassword().equals(PasswordUtil.md5(newPassword, list.get(i).getSalt()))) {
                        throw new NCException("-1", MessageFormat.format("最近{0}次密码不能重复！", ConfigCache.HISTORY_SIZE));
                    }
                } else {
                    // 删除多余的密码
                    operateAdminPasswordMapper.deleteByPrimaryKey(list.get(i).getAdminPwdId());
                }
            }
        }
    }

    /**
     * 校验更新用户密码策略
     *
     * @param list        历史密码信息
     * @param newPassword 明文新密码
     */
    @Override
    public void checkUpdatePolicyUser(List<OperateUserPassword> list, String newPassword) {
        // 1 新密码不能和现在使用的密码一样
        if (list.get(0).getPassword().equals(PasswordUtil.md5(newPassword, list.get(0).getSalt()))) {
            throw new NCException("-1", "新密码不能和现在使用的密码一样！");
        }
        // 2 最近{historySize}次历史密码重复校验限制 0-不限制（默认），5-最近5次密码不能相同
        if (0 < ConfigCache.HISTORY_SIZE) {
            for (int i = 0; i < list.size(); i++) {
                if (i < ConfigCache.HISTORY_SIZE) {
                    if (list.get(i).getPassword().equals(PasswordUtil.md5(newPassword, list.get(i).getSalt()))) {
                        throw new NCException("-1", MessageFormat.format("最近{0}次密码不能重复！", ConfigCache.HISTORY_SIZE));
                    }
                } else {
                    // 删除多余的密码
                    operateAdminPasswordMapper.deleteByPrimaryKey(list.get(i).getUserPwdId());
                }
            }
        }
    }

    /**
     * 设置登陆失败次数
     *
     * @param account
     */
    @Override
    public Integer setLoginFileNum(String account) {
        String key = RedisConstants.ADMIN_LOGIN_FAIL_NUM + account;
        Integer loginFileNum = redisUtils.get(key, Integer.class);
        loginFileNum = loginFileNum == null ? 1 : loginFileNum + 1;
        Long expire = redisUtils.getExpire(key);
        expire = expire == -2 ? 12 * 60 * 60 : expire;
        redisUtils.set(key, loginFileNum, expire);
        return loginFileNum;
    }

    /**
     * 设置登陆失败次数
     *
     * @param account
     */
    @Override
    public Integer setUserLoginFileNum(String account) {
        String key = RedisConstants.USER_LOGIN_FAIL_NUM + account;
        Integer loginFileNum = redisUtils.get(key, Integer.class);
        loginFileNum = loginFileNum == null ? 1 : loginFileNum + 1;
        Long expire = redisUtils.getExpire(key);
        expire = expire == -2 ? 12 * 60 * 60 : expire;
        redisUtils.set(key, loginFileNum, expire);
        return loginFileNum;
    }

    /**
     * 删除登录失败次数的缓存
     *
     * @param account
     */
    @Override
    public void deleteLoginFileNum(String account) {
        redisUtils.delete(RedisConstants.ADMIN_LOGIN_FAIL_NUM + account);
    }

    /**
     * 删除登录失败次数的缓存
     *
     * @param account
     */
    @Override
    public void deleteUserLoginFileNum(String account) {
        redisUtils.delete(RedisConstants.USER_LOGIN_FAIL_NUM + account);
    }

    public static void main(String[] args) {
        String p1 = "12";
        String p2 = "123456";
        String p3 = "aaaaaa";
        String p4 = "AAAAAA";
        String p5 = "12345a";
        String p6 = "12345A";
        String p7 = "12345aA";
        String p8 = "......";
        String p9 = ".....1";
        String p10 = "12345612345A";
        String t2 = "^(?![0-9]+$)(?![a-z]+$)(?![A-Z]+$)(?![\\W]+$)[0-9A-Za-z\\W]{6,}$";
        System.out.println(p1.matches(t2) + " " + p1);
        System.out.println(p2.matches(t2) + " " + p2);
        System.out.println(p3.matches(t2) + " " + p3);
        System.out.println(p4.matches(t2) + " " + p4);
        System.out.println(p5.matches(t2) + " " + p5);
        System.out.println(p6.matches(t2) + " " + p6);
        System.out.println(p7.matches(t2) + " " + p7);
        System.out.println(p8.matches(t2) + " " + p8);
        System.out.println(p9.matches(t2) + " " + p9);
        System.out.println(p10.matches(t2) + " " + p10);

    }
}
