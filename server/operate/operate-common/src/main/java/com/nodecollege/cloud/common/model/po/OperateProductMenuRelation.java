package com.nodecollege.cloud.common.model.po;

import lombok.Data;

/**
 * Table: upms_product_menu_relation
 * 版权：节点学院
 *
 * @author LC
 * @date 2020-08-17 21:09:05
 */
@Data
public class OperateProductMenuRelation {
    /**
     * 主键
     */
    private Long id;

    /**
     * 产品代码
     */
    private String productCode;

    /**
     * 菜单代码
     */
    private String menuCode;

    /**
     * 所属产品代码
     */
    private String belongCode;
}