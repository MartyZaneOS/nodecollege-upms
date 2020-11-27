package com.nodecollege.cloud.common.model.po;

import lombok.Data;

/**
 * Table: o_org_role
 * 版权：节点学院
 *
 * @author LC
 * @date 2020-09-08 21:49:25
 */
@Data
public class OperateRoleOrg {
    /**
     * 主键
     */
    private Long id;

    /**
     * 用途 0-运营/运维，1-2C
     */
    private Integer roleOrgUsage;

    /**
     * 机构代码
     */
    private String orgCode;

    /**
     * 角色代码
     */
    private String roleCode;
}