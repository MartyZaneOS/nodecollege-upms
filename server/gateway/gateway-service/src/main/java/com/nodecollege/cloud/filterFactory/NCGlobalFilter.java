package com.nodecollege.cloud.filterFactory;

import com.alibaba.fastjson.JSONObject;
import com.nodecollege.cloud.common.constants.HeaderConstants;
import com.nodecollege.cloud.common.constants.QueueConstants;
import com.nodecollege.cloud.common.constants.RedisConstants;
import com.nodecollege.cloud.common.model.NCLoginUserVO;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.po.SysLog;
import com.nodecollege.cloud.common.utils.NCLoginUtils;
import com.nodecollege.cloud.common.utils.QueueUtils;
import com.nodecollege.cloud.common.utils.RedisUtils;
import org.apache.commons.lang3.StringUtils;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.UUID;

/**
 * 全局拦截器
 * 记录日志
 *
 * @author LC
 * @date 20:34 2020/9/25
 **/
@Component
public class NCGlobalFilter implements GlobalFilter, Ordered {
    private Logger logger = LoggerFactory.getLogger(NCGlobalFilter.class);

    @Value("${spring.application.name:default}")
    private String applicationName;

    @Value("${log.push:false}")
    private Boolean logPush;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private QueueUtils queueUtils;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 开始时间
        long startTime = System.currentTimeMillis();
        NCLoginUtils.reset();
        SysLog sysLog = new SysLog();
        sysLog.setAppName(applicationName);
        sysLog.setCreateTime(new Date());

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        sysLog.setRequestIp(getIpAddress(request));
        sysLog.setAccessSource(request.getHeaders().getFirst(HeaderConstants.ACCESS_SOURCE));
        sysLog.setRequestUri(request.getPath().toString());
        if (sysLog.getRequestUri().lastIndexOf("/") == sysLog.getRequestUri().length() - 1){
            sysLog.setRequestUri(sysLog.getRequestUri().substring(0, sysLog.getRequestUri().length() - 1));
        }
        sysLog.setReferer(request.getHeaders().getFirst("Referer"));

        // 获取访问请求id 用于链路追踪
        String requestId = request.getHeaders().getFirst(HeaderConstants.REQUEST_ID);
        // 获取服务调用随机码
        String callIds = request.getHeaders().getFirst(HeaderConstants.CALL_IDS);
        if (StringUtils.isBlank(requestId)) {
            requestId = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16);
        }
        // 每个服务在后面追加字符串
        callIds = (StringUtils.isBlank(callIds) ? "" : callIds + "-") + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 3);
        sysLog.setRequestId(requestId);
        sysLog.setCallIds(callIds);
        MDC.put(HeaderConstants.REQUEST_ID, requestId);
        MDC.put(HeaderConstants.CALL_IDS, callIds);

        String adminAccessToken = null;
        String adminUuid = null;
        String userAccessToken = null;
        String userUuid = null;
        String memberAccessToken = null;
        String memberUuid = null;

        HttpCookie adminAccessCookie = request.getCookies().getFirst(HeaderConstants.ADMIN_ACCESS_TOKEN);
        HttpCookie adminUuidCookie = request.getCookies().getFirst(HeaderConstants.ADMIN_UUID);
        if (adminAccessCookie != null && adminUuidCookie != null) {
            NCLoginUserVO loginInfo = redisUtils.get(RedisConstants.ADMIN_LOGIN_INFO + adminAccessCookie.getValue(), NCLoginUserVO.class);
            logger.info("管理员 accessToken:{}, uuid: {}, account: {}", adminAccessCookie.getValue(), adminUuidCookie.getValue(), loginInfo != null ? loginInfo.getAccount() : "");
            if (loginInfo != null && loginInfo.getUuid().equals(adminUuidCookie.getValue())) {
                adminAccessToken = adminAccessCookie.getValue();
                adminUuid = adminUuidCookie.getValue();
                NCLoginUtils.setAdminLoginInfo(loginInfo);
                sysLog.setAdminId(loginInfo.getLoginId());
                sysLog.setAdminAccount(loginInfo.getAccount());
            }
        }

        HttpCookie userAccessCookie = request.getCookies().getFirst(HeaderConstants.USER_ACCESS_TOKEN);
        HttpCookie userUuidCookie = request.getCookies().getFirst(HeaderConstants.USER_UUID);
        if (userAccessCookie != null && userUuidCookie != null) {
            NCLoginUserVO loginInfo = redisUtils.get(RedisConstants.USER_LOGIN_INFO + userAccessCookie.getValue(), NCLoginUserVO.class);
            logger.info("用户 accessToken:{}, uuid: {}, account: {}", userAccessCookie.getValue(), userUuidCookie.getValue(), loginInfo != null ? loginInfo.getAccount() : "");
            if (loginInfo != null && loginInfo.getUuid().equals(userUuidCookie.getValue())) {
                userAccessToken = userAccessCookie.getValue();
                userUuid = userUuidCookie.getValue();
                NCLoginUtils.setUserLoginInfo(loginInfo);
                sysLog.setUserId(loginInfo.getLoginId());
                sysLog.setUserAccount(loginInfo.getAccount());
            }
        }

        HttpCookie memberAccessCookie = request.getCookies().getFirst(HeaderConstants.MEMBER_ACCESS_TOKEN);
        HttpCookie memberUuidCookie = request.getCookies().getFirst(HeaderConstants.MEMBER_UUID);
        if (memberAccessCookie != null && memberUuidCookie != null) {
            NCLoginUserVO loginInfo = redisUtils.get(RedisConstants.MEMBER_LOGIN_INFO + memberAccessCookie.getValue(), NCLoginUserVO.class);
            logger.info("成员 accessToken:{}, uuid: {}, account: {}", memberAccessCookie.getValue(), memberUuidCookie.getValue(), loginInfo != null ? loginInfo.getAccount() : "");
            if (loginInfo != null && loginInfo.getUuid().equals(memberUuidCookie.getValue())) {
                memberAccessToken = memberAccessCookie.getValue();
                memberUuid = memberUuidCookie.getValue();
                NCLoginUtils.setMemerLoginInfo(loginInfo);
                sysLog.setMemberId(loginInfo.getLoginId());
                sysLog.setMemberAccount(loginInfo.getAccount());
                sysLog.setTenantId(loginInfo.getTenantId());
                sysLog.setTenantCode(loginInfo.getTenantCode());
            }
        }

        StringBuffer requestStr = new StringBuffer("");
        // 获取返回值
        StringBuffer responseStr = new StringBuffer("");
        // 获取返回值
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(response) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {
                    Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
                    DataBufferFactory bufferFactory = response.bufferFactory();
                    return super.writeWith(fluxBody.map(dataBuffer -> {
                        byte[] content = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(content);
                        //释放掉内存
                        DataBufferUtils.release(dataBuffer);
                        String s = new String(content, Charset.forName("UTF-8"));
                        responseStr.append(s);
//                        byte[] uppedContent = new String(content, Charset.forName("UTF-8")).getBytes();
                        return bufferFactory.wrap(content);
                    }));
                }
                return super.writeWith(body);
            }
        };

        String finalRequestId = requestId;
        String finalCallIds = callIds;
        String finalAdminUuid = adminUuid;
        String finalAdminAccessToken = adminAccessToken;
        String finalUserUuid = userUuid;
        String finalUserAccessToken = userAccessToken;
        String finalMemberUuid = memberUuid;
        String finalMemberAccessToken = memberAccessToken;
        if (HttpMethod.POST.equals(request.getMethod())) {
            // todo bug body为空时 不返回任何信息
            return DataBufferUtils
                    .join(request.getBody())
                    .flatMap(dataBuffer -> {
                        // 获取请求参数
                        byte[] bytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(bytes);
                        try {
                            String bodyString = new String(bytes, "utf-8");
                            requestStr.append(bodyString);
                        } catch (UnsupportedEncodingException e) {
                            logger.error("获取请求报文失败！", e);
                        }
                        // 释放流
                        DataBufferUtils.release(dataBuffer);
                        // 请求参数只能读取一次，需要重新构建request
                        Flux<DataBuffer> cachedFlux = Flux.defer(() -> {
                            DataBuffer buffer = response.bufferFactory().wrap(bytes);
                            return Mono.just(buffer);
                        });
                        ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(request) {
                            @Override
                            public Flux<DataBuffer> getBody() {
                                return cachedFlux;
                            }
                        };
                        ServerHttpRequest ss = updateHeaders(mutatedRequest, finalRequestId, finalCallIds,
                                finalAdminAccessToken, finalAdminUuid, finalUserAccessToken,
                                finalUserUuid, finalMemberAccessToken, finalMemberUuid, sysLog.getTenantCode());
                        return chain.filter(exchange.mutate().request(ss).response(decoratedResponse).build())
                                .then().then(Mono.fromRunnable(() -> sentSysLog(startTime, requestStr.toString(), responseStr.toString(), sysLog)));
                    });
        } else {
            MultiValueMap<String, String> map = exchange.getRequest().getQueryParams();
            requestStr.append(JSONObject.toJSONString(map));
            ServerHttpRequest mutatedRequest = updateHeaders(request, finalRequestId, finalCallIds,
                    finalAdminAccessToken, finalAdminUuid, finalUserAccessToken, finalUserUuid,
                    finalMemberAccessToken, finalMemberUuid, sysLog.getTenantCode());
            return chain.filter(exchange.mutate().request(mutatedRequest).response(decoratedResponse).build())
                    .then().then(Mono.fromRunnable(() -> sentSysLog(startTime, requestStr.toString(), responseStr.toString(), sysLog)));
        }
    }

    @Override
    public int getOrder() {
        return -999;
    }

    /**
     * 更新请求头
     */
    private ServerHttpRequest updateHeaders(ServerHttpRequest superHeaders, String requestId, String callIds, String adminAccessToken, String adminUuid
            , String userAccessToken, String userUuid, String memberAccessToken, String memberUuid, String tenantCode) {
        ServerHttpRequest ss = superHeaders.mutate()
                .header(HeaderConstants.REQUEST_ID, requestId)
                .header(HeaderConstants.CALL_IDS, callIds)
                .header(HeaderConstants.ACCESS_SOURCE, applicationName)
                .header(HeaderConstants.ADMIN_ACCESS_TOKEN, adminAccessToken)
                .header(HeaderConstants.ADMIN_UUID, adminUuid)
                .header(HeaderConstants.USER_ACCESS_TOKEN, userAccessToken)
                .header(HeaderConstants.USER_UUID, userUuid)
                .header(HeaderConstants.MEMBER_ACCESS_TOKEN, memberAccessToken)
                .header(HeaderConstants.MEMBER_UUID, memberUuid)
                .header(HeaderConstants.TENANT_CODE, tenantCode)
                .build();
        return ss;
    }

    /**
     * 发送日志
     */
    private void sentSysLog(Long startTime, String requestBody, String responseBody, SysLog sysLog) {
        logger.debug("请求参数：{}", requestBody);
        logger.debug("返回值：{}", responseBody);
        if (requestBody.length() > 1024 * 8) {
            // 返回值太大 截取部分
            sysLog.setInParam(requestBody.substring(0, 1024 * 8));
        } else {
            sysLog.setInParam(requestBody);
        }
        if (responseBody.length() > 1024 * 8) {
            // 返回值太大 截取部分
            sysLog.setOutParam(responseBody.substring(0, 1024 * 8));
        } else {
            sysLog.setOutParam(responseBody);
        }
        try {
            NCResult result = JSONObject.parseObject(responseBody, NCResult.class);
            if (result == null) {
                sysLog.setSuccess(false);
                sysLog.setErrorMsg("返回值为空！");
            } else {
                sysLog.setSuccess(result.getSuccess());
            }
        } catch (Exception e) {
            logger.error("非标准返回值，转换异常！返回值：{}", responseBody, e);
        }
        NCLoginUtils.reset();
        sysLog.setLostTime(System.currentTimeMillis() - startTime);
        logger.info("花费:{}ms", sysLog.getLostTime());
        if (logPush) {
            try {
                queueUtils.pushTask(QueueConstants.SYS_LOG, sysLog);
            } catch (Exception e) {
                logger.error("保存日志到队列中失败！ {}", JSONObject.toJSONString(sysLog), e);
            }
        }
    }

    /**
     * 获取真实请求ip
     */
    private static String getIpAddress(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        String ip = headers.getFirst("x-forwarded-for");
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            if (ip.indexOf(",") != -1) {
                ip = ip.split(",")[0];
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddress().getAddress().getHostAddress();
        }
        return ip;
    }
}