package com.nodecollege.cloud.service.impl;

import com.nodecollege.cloud.common.constants.RedisConstants;
import com.nodecollege.cloud.common.model.po.OperateRoute;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.common.utils.RedisUtils;
import com.nodecollege.cloud.dao.mapper.OperateRouteMapper;
import com.nodecollege.cloud.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author LC
 * @date 2020/9/21 20:34
 */
@Service
public class RouteServiceImpl implements RouteService {

    @Autowired
    private OperateRouteMapper routeMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public List<OperateRoute> getRouteList(OperateRoute queryVO) {
        return routeMapper.selectList(queryVO);
    }

    @PostConstruct
    private void initRouteCache() {
        redisUtils.increment(RedisConstants.ROUTE_VERSION, 1);
        List<OperateRoute> routeList = routeMapper.selectList(new OperateRoute());
        redisUtils.set(RedisConstants.ROUTE_LIST, routeList, -1L);
    }

    @Override
    public void addRoute(OperateRoute route) {
        NCUtils.nullOrEmptyThrow(route.getRouteName());
        NCUtils.nullOrEmptyThrow(route.getRouteType());
        NCUtils.nullOrEmptyThrow(route.getRouteUrl());
        NCUtils.nullOrEmptyThrow(route.getRouteOrder());
        NCUtils.nullOrEmptyThrow(route.getPredicateJson());
        NCUtils.nullOrEmptyThrow(route.getFilterJson());

        route.setRouteId(NCUtils.getUUID());
        route.setRouteState(2);
        routeMapper.insertSelective(route);
        initRouteCache();
    }

    @Override
    public void updateRoute(OperateRoute route) {
        NCUtils.nullOrEmptyThrow(route.getId());
        NCUtils.nullOrEmptyThrow(route.getRouteState());
        route.setRouteId(null);
        routeMapper.updateByPrimaryKeySelective(route);
        initRouteCache();
    }

    @Override
    public void delRoute(OperateRoute route) {
        NCUtils.nullOrEmptyThrow(route.getId());
        routeMapper.deleteByPrimaryKey(route.getId());
        initRouteCache();
    }
}
