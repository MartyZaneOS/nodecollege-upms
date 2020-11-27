package com.nodecollege.cloud.service;

import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateRoute;
import com.nodecollege.cloud.common.model.vo.LoginVO;

import java.util.List;

/**
 * @author LC
 * @date 2020/9/21 20:34
 */
public interface RouteService {
    List<OperateRoute> getRouteList(OperateRoute queryVO);

    void addRoute(OperateRoute route);

    void updateRoute(OperateRoute route);

    void delRoute(OperateRoute route);
}
