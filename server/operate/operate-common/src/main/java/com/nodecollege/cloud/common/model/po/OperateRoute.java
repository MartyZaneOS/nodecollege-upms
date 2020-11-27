package com.nodecollege.cloud.common.model.po;

import lombok.Data;

/**
 * Table: o_route
 * 版权：节点学院
 *
 * @author LC
 * @date 2020-09-21 20:20:50
 */
@Data
public class OperateRoute {
    /**
     * id
     */
    private Long id;

    /**
     * 唯一id
     */
    private String routeId;

    /**
     * 网关名称
     */
    private String routeName;

    /**
     * 转发类型 0-从注册中心获取地址，1-直接跳转网络地址
     */
    private Integer routeType;

    /**
     * 路由地址
     */
    private String routeUrl;

    /**
     * 路由排序 越小越优先
     */
    private Integer routeOrder;
    /**
     * 路由状态 -1-配置异常，0-不可删除，1-正常，2-禁用
     */
    private Integer routeState;

    /**
     * 断言
     */
    private String predicateJson;

    /**
     * 过滤器
     */
    private String filterJson;
}