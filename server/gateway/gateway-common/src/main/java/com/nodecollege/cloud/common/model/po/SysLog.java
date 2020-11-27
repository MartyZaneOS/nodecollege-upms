package com.nodecollege.cloud.common.model.po;

import lombok.Data;

import java.util.Date;

/**
 * Table: sys_log
 * 版权：节点学院
 *
 * @author LC
 * @date 2020-07-07 20:28:43
 */
@Data
public class SysLog {
    // 主键
    private Long id;

    // 请求id
    private String requestId;

    // 线程id
    private String callIds;

    // 请求来源 nginx、appName
    private String accessSource;

    // 服务名称
    private String appName;

    // 请求地址
    private String requestUri;

    // 请求页面
    private String referer;

    // 请求ip
    private String requestIp;

    // 入参
    private String inParam;

    // 请求是否成功
    private Boolean success;

    // 出参
    private String outParam;

    // 异常信息
    private String errorMsg;

    // 耗时
    private Long lostTime;

    // 管理员id
    private Long adminId;

    // 管理员账号
    private String adminAccount;

    // 用户id
    private Long userId;

    // 用户账号
    private String userAccount;

    // 成员id
    private Long memberId;

    // 租户成员账号
    private String memberAccount;

    // 租户id
    private Long tenantId;

    // 租户代码
    private String tenantCode;

    // 创建时间
    private Date createTime;
}