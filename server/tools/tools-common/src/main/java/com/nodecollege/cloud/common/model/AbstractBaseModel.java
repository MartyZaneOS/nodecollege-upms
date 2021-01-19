package com.nodecollege.cloud.common.model;

import lombok.Data;

/**
 * @author LC
 * @date 2020/2/16 11:15
 */
@Data
public abstract class AbstractBaseModel {
    /**
     * 租户id
     */
    private Long tenantId;
    /**
     * 组织机构id
     */
    private Long orgId;
    /**
     * 成员id
     */
    private Long memberId;
    /**
     * 用户id
     */
    private Long userId;

    // 管理员id
    private Long adminId;
}
