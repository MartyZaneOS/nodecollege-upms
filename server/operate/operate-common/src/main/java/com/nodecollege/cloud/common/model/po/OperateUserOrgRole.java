package com.nodecollege.cloud.common.model.po;

import lombok.Data;

/**
 * Table: o_user_org_role
 * 版权：节点学院
 *
 * @author LC
 * @date 2020-09-30 16:28:46
 */
@Data
public class OperateUserOrgRole {
    /**
     * id
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 机构代码
     */
    private String orgCode;

    /**
     * 角色代码
     */
    private String roleCode;
}