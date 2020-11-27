package com.nodecollege.cloud.common.model.po;

import lombok.Data;

/**
 * @author LC
 * @date 2020/9/21 20:02
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
     * 路由状态 0-禁用，1-启用
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
