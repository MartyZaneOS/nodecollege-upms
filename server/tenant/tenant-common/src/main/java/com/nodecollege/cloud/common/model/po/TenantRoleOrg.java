package com.nodecollege.cloud.common.model.po;

import lombok.Data;

/**
 * Table: t_role_org
 * 版权：节点学院
 *
 * @author LC
 * @date 2020-10-16 16:05:03
 */
@Data
public class TenantRoleOrg {
    /**
     * 主键
     */
    private Long id;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 角色代码
     */
    private String roleCode;

    /**
     * 机构代码
     */
    private String orgCode;

    /**
     * 角色来源 0-预制角色，1-自定义角色
     */
    private Integer roleSource;
}