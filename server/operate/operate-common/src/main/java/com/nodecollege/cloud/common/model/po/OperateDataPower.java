package com.nodecollege.cloud.common.model.po;

import lombok.Data;

/**
 * Table: o_data_power
 * 版权：节点学院
 *
 * @author LC
 * @date 2020-12-14 18:01:09
 */
@Data
public class OperateDataPower {
    /**
     * 主键
     */
    private Long id;

    /**
     * 数据权限用途 0-运营/运维，1-2C
     */
    private Integer dataPowerUsage;

    /**
     * 权限代码
     */
    private String dataPowerCode;

    /**
     * 数据权限名称
     */
    private String dataPowerName;

    /**
     * 数据权限类型 0-机构，1-机构用户
     */
    private Integer dataPowerType;

    /**
     * 数据权限选项 json字符串
     */
    private String dataOption;
}