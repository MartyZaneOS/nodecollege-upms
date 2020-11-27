package com.nodecollege.cloud.common.model.po;

import lombok.Data;

/**
 * Table: o_admin_org_role
 * 版权：节点学院
 *
 * @author LC
 * @date 2020-09-07 20:55:04
 */
@Data
public class OperateAdminOrgRole {
    /**
     * 主键
     */
    private Long id;

    /**
     * 管理员id
     */
    private Long adminId;

    /**
     * 机构代码
     */
    private String orgCode;

    /**
     * 角色代码
     */
    private String roleCode;
}