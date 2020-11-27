package com.nodecollege.cloud.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nodecollege.cloud.common.constants.RedisConstants;
import com.nodecollege.cloud.common.model.po.OperateRoute;
import com.nodecollege.cloud.common.utils.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RouteServiceImpl implements ApplicationEventPublisherAware {

    private Logger logger = LoggerFactory.getLogger(RouteServiceImpl.class);

    @Resource
    private RouteDefinitionWriter routeDefinitionWriter;

    private ApplicationEventPublisher publisher;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 路由版本
     */
    private Integer routeVersion = new Integer(-1);
    /**
     * 历史路由信息
     */
    private Map<String, OperateRoute> routeExMap = new HashMap<>();

    /**
     * 定时更新路由信息
     */
    @Scheduled(cron = "*/30 * * * * ? ")
    private void updateRoute() {
        Integer newVersion = (Integer) redisTemplate.opsForValue().get(RedisConstants.ROUTE_VERSION);
        logger.info("定时检测路由版本变化，历史版本：{}，最新版本：{}", routeVersion, newVersion);
        // 版本相同 结束
        if (newVersion.equals(routeVersion)) return;

        // 版本不同 更新路由版本，增改路由
        routeVersion = newVersion;
        List<OperateRoute> newRouteList = redisUtils.getList(RedisConstants.ROUTE_LIST, OperateRoute.class);
        logger.info("最新的路由信息：{}", JSONObject.toJSONString(newRouteList));
        if (newRouteList == null || newRouteList.isEmpty()) {
            // 最新路由为空，清空已设置的路由信息
            routeExMap.forEach((key, value) -> this.routeDefinitionWriter.delete(Mono.just(key)).subscribe());
            routeExMap.clear();
            this.publisher.publishEvent(new RefreshRoutesEvent(this));
            return;
        }

        Map<String, Integer> tempMap = new HashMap<>();
        // 新增更新路由
        for (int i = 0; i < newRouteList.size(); i++) {
            OperateRoute route = newRouteList.get(i);
            // 路由状态为配置错误或者禁用状态，不设置
            if (route.getRouteState() == -1 || route.getRouteState() == 2) continue;

            // 添加路由
            try {
                RouteDefinition definition = buildRoute(route);
                this.routeDefinitionWriter.save(Mono.just(definition)).subscribe();
                routeExMap.put(route.getRouteId(), route);
                tempMap.put(route.getRouteId(), 0);
            } catch (Exception e) {
                logger.error("添加路由失败！", e);
                // todo 更新路由状态为-1 配置错误
            }
        }
        // 删除多余路由
        List<String> delList = new ArrayList<>();
        routeExMap.forEach((key, value) -> {
            if (!tempMap.containsKey(key)) {
                this.routeDefinitionWriter.delete(Mono.just(key)).subscribe();
                delList.add(key);
            }
        });
        delList.forEach(key -> routeExMap.remove(key));

        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }

    /**
     * 构造路由
     */
    private RouteDefinition buildRoute(OperateRoute route) {
        RouteDefinition definition = new RouteDefinition();
        definition.setId(route.getRouteId());
        if (route.getRouteType() == 0) {
            // 调用内部服务
            definition.setUri(UriComponentsBuilder.fromUriString("lb://" + route.getRouteUrl()).build().toUri());
        } else {
            // 跳转网络地址
            definition.setUri(UriComponentsBuilder.fromHttpUrl(route.getRouteUrl()).build().toUri());
        }

        // 断言
        List<PredicateDefinition> predicateList = JSONArray.parseArray(route.getPredicateJson(), PredicateDefinition.class);

        // 过滤器
        List<FilterDefinition> filterList = JSONArray.parseArray(route.getFilterJson(), FilterDefinition.class);

        if (route.getRouteType() == 0) {
            // 自定义过滤器用于权限控制，日志统计 过滤器 NCGatewayFilterFactory
            FilterDefinition ncFilter = new FilterDefinition();
            ncFilter.setName("NC");
            ncFilter.setArgs(new HashMap<>());
            ncFilter.getArgs().put("appName", route.getRouteUrl());
            filterList.add(ncFilter);
        }

        definition.setOrder(route.getRouteOrder());
        definition.setFilters(filterList);
        definition.setPredicates(predicateList);
        return definition;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }
}