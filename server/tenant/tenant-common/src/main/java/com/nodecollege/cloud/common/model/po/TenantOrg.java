package com.nodecollege.cloud.common.model.po;

import java.util.Date;
import lombok.Data;

/**
 * Table: t_org
 * 版权：节点学院
 *
 * @author LC
 * @date 2020-10-16 16:00:03
 */
@Data
public class TenantOrg {
    /**
     * 主键
     */
    private Long id;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 机构代码
     */
    private String orgCode;

    /**
     * 机构名称
     */
    private String orgName;

    /**
     * 上级机构代码
     */
    private String parentCode;

    /**
     * 排序
     */
    private Integer num;

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
}