package com.nodecollege.cloud.common.model.po;

import lombok.Data;

/**
 * Table: t_role_menu
 * 版权：节点学院
 *
 * @author LC
 * @date 2020-10-15 21:57:03
 */
@Data
public class TenantRoleMenu {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 角色代码
     */
    private String roleCode;

    /**
     * 菜单代码
     */
    private String menuCode;

    /**
     * 角色来源 0-预制角色，1-自定义角色
     */
    private Integer roleSource;
}