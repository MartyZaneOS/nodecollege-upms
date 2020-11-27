package com.nodecollege.cloud.common.model.po;

import lombok.Data;

/**
 * Table: upms_product
 * 版权：节点学院
 *
 * @author LC
 * @date 2020-08-17 20:18:43
 */
@Data
public class OperateProduct {
    /**
     * 主键
     */
    private Long id;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 产品代码
     */
    private String productCode;

    /**
     * 产品类型：0-顶级产品，1-共存式产品，2-排斥式产品
     */
    private Integer productType;

    /**
     * 产品用途：0-运营/运维，1-2C，2-2B
     */
    private Integer productUsage;

    /**
     * 父级产品代码
     */
    private String parentCode;

    /**
     * 产品描述
     */
    private String productDesc;

    /**
     * 产品图标
     */
    private String productIcon;

    /**
     * 顶级产品代码
     */
    private String topCode;

    /**
     * 归属产品代码
     */
    private String belongCode;
}