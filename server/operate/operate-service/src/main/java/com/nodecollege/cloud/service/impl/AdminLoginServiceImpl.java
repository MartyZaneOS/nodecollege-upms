package com.nodecollege.cloud.service.impl;

import com.nodecollege.cloud.common.constants.RedisConstants;
import com.nodecollege.cloud.common.enums.ErrorEnum;
import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.NCLoginUserVO;
import com.nodecollege.cloud.common.model.PowerVO;
import com.nodecollege.cloud.common.model.po.OperateAdmin;
import com.nodecollege.cloud.common.model.po.OperateAdminPassword;
import com.nodecollege.cloud.common.model.vo.LoginVO;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.common.utils.PasswordUtil;
import com.nodecollege.cloud.common.utils.RedisUtils;
import com.nodecollege.cloud.dao.mapper.OperateAdminPasswordMapper;
import com.nodecollege.cloud.service.AdminLoginService;
import com.nodecollege.cloud.service.AdminService;
import com.nodecollege.cloud.service.PasswordPolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

/**
 * @author LC
 * @date 2019/12/1 12:14
 */
@Service
public class AdminLoginServiceImpl implements AdminLoginService {

    private static Integer expire = 2 * 60 * 60;

    @Autowired
    private PasswordPolicyService passwordPolicyService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private OperateAdminPasswordMapper adminPasswordMapper;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 管理员登陆
     *
     * @param loginVO
     */
    @Override
    public NCLoginUserVO login(LoginVO loginVO) {
        // 1 登陆前验证密码策略
        passwordPolicyService.checkLoginPolicy(loginVO.getAccount(), loginVO.getPassword(),
                loginVO.getPassword(), loginVO.getImageCert());

        // 2 验证密码是否正确
        OperateAdmin admin = adminService.getAdminByAccount(loginVO.getAccount());
        List<OperateAdminPassword> passwordList = adminPasswordMapper.selectListByAdminId(admin.getAdminId());

        if (passwordList == null || passwordList.isEmpty() || !passwordList.get(0).getPassword()
                .equals(PasswordUtil.md5(loginVO.getPassword(), passwordList.get(0).getSalt()))) {
            Integer loginFileNum = passwordPolicyService.setLoginFileNum(loginVO.getAccount());
            throw new NCException("-1", MessageFormat.format("密码错误！请重新输入！(第{0}次)", loginFileNum));
        }

        // 3 登陆成功后校验密码策略
        passwordPolicyService.checkLoginSuccessPolicy(admin, passwordList, loginVO.getNewPassword(), loginVO.getImageCert());

        // 4 生成token
        String accessToken = NCUtils.getUUID();

        PowerVO powerVO = adminService.getAdminPower(admin);

        NCLoginUserVO adminLoginInfo = new NCLoginUserVO(powerVO);
        adminLoginInfo.setLoginId(admin.getAdminId());
        adminLoginInfo.setAccount(admin.getAccount());
        adminLoginInfo.setNickname(admin.getAccount());
        adminLoginInfo.setAccessToken(accessToken);
        adminLoginInfo.setUuid(NCUtils.getUUID());
        adminLoginInfo.setExpire(expire);

        redisUtils.set(RedisConstants.ADMIN_LOGIN_INFO + accessToken, adminLoginInfo, expire);

        // 清除登录失败次数缓存
        passwordPolicyService.deleteLoginFileNum(loginVO.getAccount());
        return adminLoginInfo;
    }

    @Override
    public NCLoginUserVO getAdminInfoByToken(LoginVO loginVO) {
        NCUtils.nullOrEmptyThrow(loginVO.getToken());
        return redisUtils.get(RedisConstants.ADMIN_LOGIN_INFO + loginVO.getToken(), NCLoginUserVO.class);
    }

    /**
     * 退出登陆
     *
     * @param token
     */
    @Override
    public void logout(String token) {
        redisUtils.delete(RedisConstants.ADMIN_LOGIN_INFO + token);
    }
}
