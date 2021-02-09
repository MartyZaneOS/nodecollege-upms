package com.nodecollege.cloud.service.impl;

import com.nodecollege.cloud.common.constants.RedisConstants;
import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.NCLoginUserVO;
import com.nodecollege.cloud.common.model.PowerVO;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateUser;
import com.nodecollege.cloud.common.model.vo.LoginVO;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.common.utils.PasswordUtil;
import com.nodecollege.cloud.common.utils.RedisUtils;
import com.nodecollege.cloud.dao.mapper.OperateUserMapper;
import com.nodecollege.cloud.service.LoginService;
import com.nodecollege.cloud.service.SendMailService;
import com.nodecollege.cloud.service.PasswordPolicyService;
import com.nodecollege.cloud.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

/**
 * @author LC
 * @date 2019/6/21 18:22
 */
@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

    private static Integer expire = 2 * 60 * 60;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private OperateUserMapper operateUserMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordPolicyService passwordPolicyService;

    @Autowired
    private SendMailService sendMailService;

    /**
     * 用户注册
     *
     * @param loginVO
     */
    @Override
    public void register(LoginVO loginVO) {
        NCUtils.nullOrEmptyThrow(loginVO.getImageCert());
        NCUtils.nullOrEmptyThrow(loginVO.getEmail());
        if (!sendMailService.checkEmailCert(loginVO.getEmail(), loginVO.getImageCert())) {
            throw new NCException("", "邮箱验证码不正确！");
        }
        userService.addUser(loginVO);
        sendMailService.delEmailCert(loginVO.getEmail());
    }

    /**
     * 用户登录
     *
     * @param loginVO
     */
    @Override
    public NCLoginUserVO login(LoginVO loginVO) {
        // 1 登陆前验证密码策略
        passwordPolicyService.checkUserLoginPolicy(loginVO.getAccount(), loginVO.getPassword(), loginVO.getPassword(), loginVO.getImageCert());

        // 2 验证密码是否正确
        QueryVO<OperateUser> queryLoginUser = new QueryVO<>(new OperateUser());
        queryLoginUser.getData().setAccount(loginVO.getAccount());
        List<OperateUser> result = operateUserMapper.selectUserListByMap(queryLoginUser.toMap());
        if (result.isEmpty()) {
            queryLoginUser.getData().setAccount(null);
            queryLoginUser.getData().setTelephone(loginVO.getAccount());
            result = operateUserMapper.selectUserListByMap(queryLoginUser.toMap());
        }
        if (result.isEmpty()) {
            queryLoginUser.getData().setTelephone(null);
            queryLoginUser.getData().setEmail(loginVO.getAccount());
            result = operateUserMapper.selectUserListByMap(queryLoginUser.toMap());
        }
        NCUtils.nullOrEmptyThrow(result, new NCException("-1", "未获取到用户信息"));
        OperateUser user = result.get(0);
        if (!user.getPassword().equals(PasswordUtil.md5(loginVO.getPassword(), user.getSalt()))) {
            Integer loginFileNum = passwordPolicyService.setUserLoginFileNum(loginVO.getAccount());
            throw new NCException("-1", MessageFormat.format("密码错误！请重新输入！(第{0}次)", loginFileNum));
        }

        // 3 登陆成功后校验密码策略
        Boolean checkPasswordValidity = passwordPolicyService.checkUserLoginSuccessPolicy(user.getUserId(), user.getFirstLogin());
        if (!checkPasswordValidity) {
            if (StringUtils.isBlank(loginVO.getNewPassword()) || StringUtils.isBlank(loginVO.getImageCert())) {
                throw new NCException("10001", "首次登陆，或者密码已过密码有效期，请重新设置新密码！");
            }
        }

        // 4 生成token
        // 访问令牌
        String accessToken = NCUtils.getUUID();
        // 成员信息令牌 当成员信息令牌改变时，说明成员信息有更新，需重新获取用户信息
        user = userService.getUserByUserId(user.getUserId());
        PowerVO powerVO = userService.getUserPower(user);

        NCLoginUserVO loginUser = new NCLoginUserVO(powerVO);
        loginUser.setLoginId(user.getUserId());
        loginUser.setAccount(user.getAccount());
        loginUser.setNickname(StringUtils.isBlank(user.getNickname()) ? user.getAccount() : user.getNickname());
        loginUser.setAvatar(user.getAvatar());
        loginUser.setAccessToken(accessToken);
        loginUser.setUuid(NCUtils.getUUID());
        loginUser.setExpire(expire);
        loginUser.setTenantId(user.getTenantId());
        loginUser.setAvatar(user.getAvatar());
        loginUser.setAvatarThumb(user.getAvatarThumb());

        // 初始化用户信息数据权限信息
        redisUtils.set(RedisConstants.USER_LOGIN_INFO + accessToken, loginUser, expire);

        // 清除登录失败次数缓存
        passwordPolicyService.deleteUserLoginFileNum(loginVO.getAccount());

        return loginUser;
    }

    @Override
    public NCLoginUserVO getLoginUserInfo(LoginVO loginVO) {
        NCUtils.nullOrEmptyThrow(loginVO.getToken());
        return redisUtils.get(RedisConstants.USER_LOGIN_INFO + loginVO.getToken(), NCLoginUserVO.class);
    }

    /**
     * 注销登录
     *
     * @param token
     */
    @Override
    public void logout(String token) {
        redisUtils.delete(RedisConstants.USER_LOGIN_INFO + token);
    }

    /**
     * 根据token获取用户信息
     *
     * @param token
     * @return
     */
    @Override
    public NCLoginUserVO getUserInfo(String token) {
        return redisUtils.get(RedisConstants.USER_LOGIN_INFO + token, NCLoginUserVO.class);
    }

    /**
     * 未登陆情况下修改密码
     *
     * @param loginVO
     */
    @Override
    public void updatePwdNoLogin(LoginVO loginVO) {

        NCUtils.nullOrEmptyThrow(loginVO.getAccount(), new NCException("-1", "账号必填！"));
        NCUtils.nullOrEmptyThrow(loginVO.getPassword(), new NCException("-1", "旧密码必填！"));
        NCUtils.nullOrEmptyThrow(loginVO.getNewPassword(), new NCException("-1", "新密码必填！"));

        // 1 登陆前验证密码策略
        passwordPolicyService.checkLoginPolicy(loginVO.getAccount(), loginVO.getPassword(),
                loginVO.getPassword(), loginVO.getImageCert());

        // 2 验证密码是否正确
        OperateUser user = userService.getUserByAccount(loginVO.getAccount());

        if (!user.getPassword().equals(PasswordUtil.md5(loginVO.getPassword(), user.getSalt()))) {
            Integer loginFileNum = passwordPolicyService.setLoginFileNum(loginVO.getAccount());
            throw new NCException("-1", MessageFormat.format("密码错误！请重新输入！(第{0}次)", loginFileNum));
        }

        // 更新密码
        userService.updatePwd(user.getUserId(), loginVO.getPassword(), loginVO.getNewPassword());
    }
}
