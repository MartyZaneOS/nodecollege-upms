package com.nodecollege.cloud.service;

import com.nodecollege.cloud.common.model.NCLoginUserVO;
import com.nodecollege.cloud.common.model.vo.LoginVO;

/**
 * @author LC
 * @date 2019/6/21 18:22
 */
public interface LoginService {

    /**
     * 用户登陆
     *
     * @param loginVO
     */
    NCLoginUserVO login(LoginVO loginVO);

    /**
     * 用户注销
     *
     * @return
     */
    void logout(String token);

    /**
     * 用户注册/保存
     *
     * @param loginVO
     */
    void register(LoginVO loginVO);

    /**
     * 获取当前登陆用户拥有的资源信息
     *
     * @param token
     */
    NCLoginUserVO getUserInfo(String token);

    /**
     * 未登陆情况下修改密码
     *
     * @param loginVO
     */
    void updatePwdNoLogin(LoginVO loginVO);

    NCLoginUserVO getLoginUserInfo(LoginVO loginVO);
}
