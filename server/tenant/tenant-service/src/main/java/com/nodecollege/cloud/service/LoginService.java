package com.nodecollege.cloud.service;

import com.nodecollege.cloud.common.model.NCLoginUserVO;
import com.nodecollege.cloud.common.model.po.TenantMember;
import com.nodecollege.cloud.common.model.vo.LoginVO;

/**
 * @author LC
 * @date 2020/10/21 20:04
 */
public interface LoginService {

    NCLoginUserVO login(LoginVO loginVO, String loginTenantCode);

    NCLoginUserVO loginByUserTenant(TenantMember member);

    void logout(String accessToken);

    void updatePwdNoLogin(LoginVO loginVO, String tenantCode);
}
