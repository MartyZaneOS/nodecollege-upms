package com.nodecollege.cloud.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nodecollege.cloud.client.utils.NCLoginUtils;
import com.nodecollege.cloud.common.annotation.ApiAnnotation;
import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.BindVO;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateAppApi;
import com.nodecollege.cloud.common.model.vo.AppApiTreeVO;
import com.nodecollege.cloud.common.model.vo.DataPowerVO;
import com.nodecollege.cloud.common.model.vo.InterfaceTreeVO;
import com.nodecollege.cloud.service.AppApiService;
import com.nodecollege.cloud.service.DataPowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author LC
 * @date 2020/8/26 19:59
 */
@RestController
@RequestMapping("/appApi")
public class AppApiController {

    @Autowired
    private AppApiService appApiService;

    @Autowired
    private NCLoginUtils loginUtils;

    @Autowired
    private DataPowerService dataPowerService;

    @ApiAnnotation(modularName = "微服务接口管理", description = "查询微服务接口列表")
    @PostMapping("/getAppApiList")
    public NCResult<OperateAppApi> getAppApiList(@RequestBody QueryVO<OperateAppApi> queryVO) {
        List<String> authList = getAdminAuth("appDataPower");
        if (authList == null) {
            throw new NCException("", "权限不足！");
        }
        queryVO.setStringList(authList);
        Page page = PageHelper.startPage(queryVO.getPageNum(), queryVO.getPageSize());
        List<OperateAppApi> list = appApiService.getAppApiList(queryVO);
        return NCResult.ok(list, page.getTotal());
    }

    @ApiAnnotation(modularName = "微服务接口管理", description = "查询微服务接口树")
    @PostMapping("/getAppApiTree")
    public NCResult<InterfaceTreeVO> getAppApiTree() {
        return NCResult.ok(appApiService.getApiTree());
    }


    @ApiAnnotation(modularName = "微服务接口管理", description = "添加接口", accessSource = 2)
    @PostMapping("/initApiList")
    public NCResult<OperateAppApi> initApiList(@RequestBody List<OperateAppApi> annList) {
        return NCResult.ok(appApiService.initApiList(annList));
    }

    @ApiAnnotation(modularName = "微服务接口管理", description = "更新接口授权信息")
    @PostMapping("/syncAllAppApiAccessAuth")
    public NCResult<AppApiTreeVO> syncAllAppApiAccessAuth() {
        appApiService.syncAllAppApiAccessAuth();
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "微服务接口管理", description = "更新接口授权信息")
    @PostMapping("/updateAppApiAccessAuth")
    public NCResult<AppApiTreeVO> updateAppApiAccessAuth(@RequestBody OperateAppApi appApi) {
        appApiService.updateAppApiAccessAuth(appApi);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "微服务接口管理", description = "删除接口")
    @PostMapping("/delAppApi")
    public NCResult<AppApiTreeVO> delAppApi(@RequestBody OperateAppApi appApi) {
        appApiService.delApi(appApi);
        return NCResult.ok();
    }

    private List<String> getAdminAuth(String dataPowerCode) {
        DataPowerVO query = new DataPowerVO();
        query.setOrgCode(loginUtils.getPowerAdminOrgCodeList());
        query.setUserId(loginUtils.getAdminLoginInfo().getLoginId());
        query.setDataPowerCode(dataPowerCode);
        query.setDataPowerUsage(0);
        return dataPowerService.getUserAuth(query);
    }
}
