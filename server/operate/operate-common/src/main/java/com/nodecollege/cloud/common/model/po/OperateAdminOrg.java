package com.nodecollege.cloud.common.model.po;

import lombok.Data;

/**
 * Table: o_admin_org
 * 版权：节点学院
 *
 * @author LC
 * @date 2020-09-10 20:33:51
 */
@Data
public class OperateAdminOrg {
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
}