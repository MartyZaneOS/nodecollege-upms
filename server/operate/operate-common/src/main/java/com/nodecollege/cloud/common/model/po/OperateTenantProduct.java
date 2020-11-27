package com.nodecollege.cloud.common.model.po;

import java.util.Date;
import lombok.Data;

/**
 * Table: o_tenant_product
 * 版权：节点学院
 *
 * @author LC
 * @date 2020-10-19 10:20:04
 */
@Data
public class OperateTenantProduct {
    /**
     * id
     */
    private Long id;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 产品代码
     */
    private String productCode;

    /**
     * 状态 0-停用，1-启用
     *
     * state
     */
    private Integer state;

    /**
     * 创建用户
     *
     * create_user
     */
    private String createUser;

    /**
     * 创建时间
     *
     * create_time
     */
    private Date createTime;

    /**
     * 修改用户
     *
     * update_user
     */
    private String updateUser;

    /**
     * 修改时间
     *
     * update_time
     */
    private Date updateTime;
}