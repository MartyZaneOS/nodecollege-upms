package com.nodecollege.cloud.common.model.po;

import lombok.Data;

/**
 * Table: o_role_menu
 * 版权：节点学院
 *
 * @author LC
 * @date 2020-08-31 20:23:12
 */
@Data
public class OperateRoleMenu {
    /**
     * 主键
     */
    private Long id;

    /**
     * 角色菜单用途 0-运营/运维，1-2C，2-2B
     */
    private Integer roleMenuUsage;

    /**
     * 角色代码
     */
    private String roleCode;

    /**
     * 菜单代码
     */
    private String menuCode;
}