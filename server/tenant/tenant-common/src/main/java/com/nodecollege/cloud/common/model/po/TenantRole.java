package com.nodecollege.cloud.common.model.po;

import java.util.Date;
import lombok.Data;

/**
 * Table: t_role
 * 版权：节点学院
 *
 * @author LC
 * @date 2020-10-16 16:02:21
 */
@Data
public class TenantRole {
    /**
     * 主键
     */
    private Long id;

    /**
     * 租户id
     */
    private Long tenantId;

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
     * 角色类型2B用途有效  0-组织角色，1-组织成员角色
     */
    private Integer roleType;

    /**
     * 数据权限 0-所有数据，1-所属及下级机构数据，2-所属机构数据，3-用户自己的数据
     */
    private Integer dataPower;

    /**
     * 状态 -1-已删除，0-正常，1-禁用
     */
    private Integer roleState;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private String updateUser;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 角色来源 0-预制角色，1-自定义角色
     * 不存库
     */
    private Integer roleSource;
}