package com.nodecollege.cloud.common.model.po;

import lombok.Data;

/**
 * Table: o_role
 * 版权：节点学院
 *
 * @author LC
 * @date 2020-08-31 20:22:36
 */
@Data
public class OperateRole {
    /**
     * 主键
     */
    private Long id;

    /**
     * 归属产品代码
     */
    private String productCode;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色代码
     */
    private String roleCode;

    /**
     * 角色描述
     */
    private String roleDesc;

    /**
     * 角色用途：0-运营/运维，1-2C，2-2B
     */
    private Integer roleUsage;

    /**
     * 角色类型 0-组织角色，1-组织成员角色
     */
    private Integer roleType;

    /**
     * 数据权限
     * 0-所有机构
     * 1-所属机构及下级机构
     * 2-所属机构
     * 3-用户自己的数据
     */
    private Integer dataPower;

    /**
     * 状态 -1-已删除，0-正常，1-禁用
     */
    private Integer roleState;
}