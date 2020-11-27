package com.nodecollege.cloud.common.model.po;

import lombok.Data;

import java.util.Date;

/**
 * Table: visit_log
 * 版权：节点学院
 *
 * @author LC
 * @date 2020-07-07 20:28:43
 */
@Data
public class VisitLog {
    // 主键
    private Long id;

    // 请求id
    private String requestId;

    // 线程id
    private String callIds;

    // 服务名称
    private String appName;

    // 请求地址
    private String requestUrl;

    // 请求域名
    private String origin;

    // 请求页面
    private String referer;

    // 请求ip
    private String requestIp;

    // 请求来源 nginx、appName
    private String source;

    // 管理员id
    private Long adminId;

    // 用户id
    private Long userId;

    // 成员id
    private Long memberId;

    // 机构id
    private Long orgId;

    // 租户id
    private Long tenantId;

    // 创建时间
    private Date createTime;
}