package com.nodecollege.cloud.common.model.po;

import lombok.Data;

import java.util.Date;

/**
 * Table: o_org
 * 版权：节点学院
 *
 * @author LC
 * @date 2020-09-08 20:08:01
 */
@Data
public class OperateOrg {
    /**
     * 主键
     */
    private Long id;

    /**
     * 机构用途 0-运维/运营，1-2C
     */
    private Integer orgUsage;

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
     * 创建时间
     */
    private Date createTime;
}