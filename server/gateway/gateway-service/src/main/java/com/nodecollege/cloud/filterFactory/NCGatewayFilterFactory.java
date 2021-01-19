package com.nodecollege.cloud.filterFactory;

import com.alibaba.fastjson.JSONObject;
import com.nodecollege.cloud.common.constants.RedisConstants;
import com.nodecollege.cloud.common.model.MenuVO;
import com.nodecollege.cloud.common.model.NCLoginUserVO;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.po.OperateAppApi;
import com.nodecollege.cloud.common.utils.NCLoginUtils;
import com.nodecollege.cloud.common.utils.RedisUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 网关过滤器
 * 处理授权信息和日志统计
 *
 * @author LC
 * @date 20:38 2020/9/23
 **/
@Component
@Order(-999)
public class NCGatewayFilterFactory extends AbstractGatewayFilterFactory<NCGatewayFilterFactory.Config> {

    private Logger logger = LoggerFactory.getLogger(NCGatewayFilterFactory.class);

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private RedisTemplate redisTemplate;

    // 接口权限版本
    private Integer apiVersion = new Integer(-1);
    // 接口权限信息
    private Map<String, String> apiPowerMap = new ConcurrentHashMap<>();

    public NCGatewayFilterFactory() {
        super(Config.class);
    }

    /**
     * 定时更新接口权限
     */
    @Scheduled(cron = "*/30 * * * * ? ")
    private void updateApiPower() {
        Integer newVersion = (Integer) redisTemplate.opsForValue().get(RedisConstants.APP_API_VERSION);
        logger.info("定时检测接口权限版本变化，历史版本：{}，最新版本：{}", apiVersion, newVersion);
        // 版本相同 结束
        if (newVersion.equals(apiVersion)) return;

        // 版本不同 更新
        apiVersion = newVersion;

        // 获取api列表
        List<OperateAppApi> list = redisUtils.getList(RedisConstants.APP_API_LIST, OperateAppApi.class);

        // 为空 清空结束
        if (list == null || list.isEmpty()) {
            apiPowerMap.clear();
            logger.info("接口权限更新完毕！");
            return;
        }

        // 更新接口信息
        Map<String, String> newMap = new ConcurrentHashMap<>();
        for (OperateAppApi api : list) {
            newMap.put("/" + api.getApplicationName() + api.getApiUrl(), api.getAccessAuth());
        }
        apiPowerMap = newMap;
        logger.info("接口权限更新完毕！");
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("appName");
    }

    /**
     * 配置信息
     */
    @Data
    public static class Config {
        // 微服务名称
        private String appName;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpResponse response = exchange.getResponse();
            ServerHttpRequest request = exchange.getRequest();

            // 请求基本信息
            String origin = request.getHeaders().getFirst("Origin");
            String referer = request.getHeaders().getFirst("Referer");
            // 接口地址
            String apiUrl = request.getPath().toString();
            // 访问授权信
            String accessAuth = apiPowerMap.get("/" + config.getAppName() + apiUrl);
            logger.info("referer: {}, appName: {}, apiUrl: {}, accessAuth: {}", referer, config.getAppName(), apiUrl, accessAuth);

            // 无授权信息 或者授权为none 直接通过
            if (StringUtils.isBlank(accessAuth) || accessAuth.indexOf("none") >= 0) {
                logger.info("无授权限制，直接通过！");
                return chain.filter(exchange);
            }

            // 只能内部调用，返回无权限访问
            if (accessAuth.indexOf("inner") >= 0) {
                logger.info("只能内部调用！");
                return responseFailRs(exchange, NCResult.error("-1", "只能内部调用！"));
            }

            // 页面路径
            String pagePath = "";
            if (referer != null && origin != null) {
                referer = referer.split("\\?")[0];
                pagePath = referer.substring(origin.length());
            }
            // 需要管理员登录或者授权
            NCLoginUserVO loginAdmin = null;
            if (accessAuth.indexOf("admin") >= 0) {
                loginAdmin = NCLoginUtils.getAdminLoginInfo();
                if (loginAdmin != null) {
                    // todo 延长登录有效期
                    if (accessAuth.indexOf("adminLogin") >= 0 ||
                            (accessAuth.indexOf("adminAuth") >= 0 && checkApiPower(loginAdmin.getMenuTree(), pagePath, config.getAppName(), apiUrl))) {
                        logger.info("管理员授权校验通过！");
                        return chain.filter(exchange.mutate().build());
                    }
                }
            }

            // 需要用户登录或者授权
            NCLoginUserVO loginUser = null;
            if (accessAuth.indexOf("user") >= 0) {
                loginUser = NCLoginUtils.getUserLoginInfo();
                if (loginUser != null) {
                    // todo 延迟登录有效期
                    if (accessAuth.indexOf("userLogin") >= 0 ||
                            (accessAuth.indexOf("userAuth") >= 0 && checkApiPower(loginUser.getMenuTree(), pagePath, config.getAppName(), apiUrl))) {
                        logger.info("用户授权校验通过！");
                        return chain.filter(exchange.mutate().build());
                    }
                    return responseFailRs(exchange, NCResult.error("80000000", "登录超时，请重新登录！"));
                }
            }

            // 需要租户成员登录或者授权
            NCLoginUserVO loginMember = null;
            if (accessAuth.indexOf("member") >= 0) {
                loginMember = NCLoginUtils.getMemberLoginInfo();
                if (loginMember != null) {
                    // todo 延迟登录有效期
                    if (accessAuth.indexOf("memberLogin") >= 0 ||
                            (accessAuth.indexOf("memberAuth") >= 0 && checkApiPower(loginMember.getMenuTree(), pagePath, config.getAppName(), apiUrl))) {
                        logger.info("租户成员授权校验通过！");
                        return chain.filter(exchange.mutate().build());
                    }
                }
            }

            if (loginAdmin == null && loginUser == null && loginMember == null) {
                return responseFailRs(exchange, NCResult.error("80000000", "登录超时，请重新登录！"));
            }

            // 返回无授权信息
            logger.info("无授权信息！");
            return responseFailRs(exchange, NCResult.error("80000001", "无权访问！"));
        };
    }

    /**
     * 根据页面路径校验页面权限
     */
    private Boolean checkApiPower(List<MenuVO> menuTree, String pagePath, String appName, String apiUrl) {
        if (menuTree == null) return false;
        for (MenuVO menu : menuTree) {
            if (menu.getMenuType() == 1 && menu.getPagePath().equals(pagePath))
                return checkApiUrl(menu.getChildren(), appName, apiUrl);
            if (checkApiPower(menu.getChildren(), pagePath, appName, apiUrl)) return true;
        }
        return false;
    }

    /**
     * 校验页面下按钮接口权限
     */
    private Boolean checkApiUrl(List<MenuVO> menuTree, String appName, String apiUrl) {
        if (menuTree == null) return false;
        for (MenuVO button : menuTree) {
            if (button.getAppName().equals(appName) && button.getApiUrl().equals(apiUrl))
                return true;
            if (checkApiUrl(button.getChildren(), appName, apiUrl)) return true;
        }
        return false;
    }


    private Mono<Void> responseFailRs(ServerWebExchange exchange, NCResult result) {
        ServerHttpResponse serverHttpResponse = exchange.getResponse();
        serverHttpResponse.setStatusCode(HttpStatus.OK);
        serverHttpResponse.getHeaders().setContentType(MediaType.APPLICATION_JSON_UTF8);
        byte[] bytes = JSONObject.toJSONString(result).getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return serverHttpResponse.writeWith(Flux.just(buffer));
    }
}