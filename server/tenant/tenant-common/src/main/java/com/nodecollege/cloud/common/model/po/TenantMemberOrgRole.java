package com.nodecollege.cloud.common.model.po;

import lombok.Data;

/**
 * Table: t_member_org_role
 * 版权：节点学院
 *
 * @author LC
 * @date 2020-10-15 21:53:56
 */
@Data
public class TenantMemberOrgRole {
    /**
     * 主键
     */
    private Long id;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 成员id
     */
    private Long memberId;

    /**
     * 机构代码
     */
    private String orgCode;

    /**
     * 角色代码
     */
    private String roleCode;

    /**
     * 角色来源 0-预制角色，1-自定义角色
     */
    private Integer roleSource;
}