package com.nodecollege.cloud.service;

import com.nodecollege.cloud.common.model.NCLoginUserVO;
import com.nodecollege.cloud.common.model.vo.LoginVO;

/**
 * 管理员登陆Service
 *
 * @author LC
 * @date 2019/12/1 12:13
 */
public interface AdminLoginService {

    /**
     * 管理员登陆
     *
     * @param loginVO
     * @return
     */
    NCLoginUserVO login(LoginVO loginVO);

    /**
     * 退出登陆
     *
     * @param token
     */
    void logout(String token);
}
