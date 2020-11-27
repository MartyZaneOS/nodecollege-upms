package com.nodecollege.cloud.service;

import com.nodecollege.cloud.common.model.po.OperateTenant;

/**
 * @author LC
 * @date 2020/10/20 14:33
 */
public interface TenantService {
    /**
     * 初始化租户信息
     */
    void init(OperateTenant tenant);
}
