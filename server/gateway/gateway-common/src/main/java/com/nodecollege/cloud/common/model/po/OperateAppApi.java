package com.nodecollege.cloud.common.model.po;

import lombok.Data;

/**
 * Table: upms_interface
 * 版权：节点学院
 *
 * @author LC
 * @date 2019-12-20 18:22:28
 */
@Data
public class OperateAppApi {
    // 接口id
    private Long apiId;
    // 接口应用名称
    private String applicationName;
    // controller_name
    private String controllerName;
    // 请求方法
    private String method;
    // 接口地址
    private String apiUrl;
    // 请求实体
    private String requestBody;
    // 返回实体
    private String responseBody;
    // 模块名称
    private String modularName;
    // 描述
    private String description;
    /**
     * 访问授权
     * {
     * "inner": true,     // 服务间调用
     * "none": true,      // 无限制
     * "adminAuth": true, // 管理员授权访问
     * "adminLogin": true,// 管理员登录访问
     * "userAuth": true,  // 用户授权访问
     * "userLogin": true, // 用户登录访问
     * "memberAuth": true,// 租户成员授权访问
     * "memberLogin": true// 租户成员登录访问
     * }
     */
    private String accessAuth;
    // 状态 -1-不可用，1-正常，2-后期删除，
    private Integer state;
}