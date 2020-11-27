package com.nodecollege.cloud.common.constants;

/**
 * @author LC
 * @date 2020/8/24 17:11
 */
public class HeaderConstants {
    // 请求id
    public final static String REQUEST_ID = "requestId";
    // 调用ids
    public final static String CALL_IDS = "callIds";

    // 用户访问token
    public final static String USER_ACCESS_TOKEN = "USER-ACCESS-TOKEN";
    // 用户随机吗
    public final static String USER_UUID = "USER-UUID";
    // 租户成员访问token
    public final static String MEMBER_ACCESS_TOKEN = "MEMBER-ACCESS-TOKEN";
    // 租户成员随机码
    public final static String MEMBER_UUID = "MEMBER-UUID";
    // 租户代码
    public final static String TENANT_CODE = "TENANT-CODE";

    // 运营/运维管理员访问token
    public final static String ADMIN_ACCESS_TOKEN = "ADMIN-ACCESS-TOKEN";
    // 运营/运维管理员随机码
    public final static String ADMIN_UUID = "ADMIN-UUID";

    // 访问来源
    public final static String ACCESS_SOURCE = "ACCESS-SOURCE";
    public final static String HEADER_ACCESS_SOURCE = "nc-access-source";
    // 访问来源-nginx
    public static final String NC_NGINX = "NC-NGINX";
}
