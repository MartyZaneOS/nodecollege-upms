package com.nodecollege.cloud.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nodecollege.cloud.common.annotation.ApiAnnotation;
import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateRole;
import com.nodecollege.cloud.common.model.po.OperateRoute;
import com.nodecollege.cloud.common.model.vo.LoginVO;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LC
 * @date 2020/9/21 16:35
 */
@RestController
@RequestMapping("/route")
public class RouteController {

    @Autowired
    private RouteService routeService;

    @ApiAnnotation(modularName = "路由管理", description = "获取路由列表")
    @PostMapping("/getRouteList")
    public NCResult<OperateRoute> getRouteList(@RequestBody QueryVO<OperateRoute> queryVO) {
        List<OperateRoute> list = new ArrayList<>();
        Long total = 0L;
        if (NCConstants.INT_NEGATIVE_1.equals(queryVO.getPageSize())) {
            list = routeService.getRouteList(queryVO.getData());
            total = NCUtils.isNullOrEmpty(list) ? 0 : Long.parseLong(list.size() + "");
        } else {
            Page page = PageHelper.startPage(queryVO.getPageNum(), queryVO.getPageSize());
            if (queryVO.isSort()) {
                page.setOrderBy(queryVO.getSortKey() + " " + queryVO.getSortDirection());
            }
            list = routeService.getRouteList(queryVO.getData());
            total = page.getTotal();
        }
        return NCResult.ok(list, total);
    }

    @ApiAnnotation(modularName = "路由管理", description = "新增路由信息")
    @PostMapping("/addRoute")
    public NCResult addRoute(@RequestBody OperateRoute route) {
        routeService.addRoute(route);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "路由管理", description = "更新路由")
    @PostMapping("/updateRoute")
    public NCResult updateRoute(@RequestBody OperateRoute route) {
        routeService.updateRoute(route);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "路由管理", description = "删除路由")
    @PostMapping("/delRoute")
    public NCResult delRoute(@RequestBody OperateRoute route) {
        routeService.delRoute(route);
        return NCResult.ok();
    }
}
