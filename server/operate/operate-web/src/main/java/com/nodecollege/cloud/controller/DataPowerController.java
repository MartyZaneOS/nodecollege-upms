package com.nodecollege.cloud.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nodecollege.cloud.client.utils.NCLoginUtils;
import com.nodecollege.cloud.common.annotation.ApiAnnotation;
import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateDataPower;
import com.nodecollege.cloud.common.model.po.OperateDataPowerAuth;
import com.nodecollege.cloud.common.model.vo.DataPowerVO;
import com.nodecollege.cloud.common.utils.RedisUtils;
import com.nodecollege.cloud.service.DataPowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author LC
 * @date 2020/12/14 18:15
 */
@RestController
@RequestMapping("/dataPower")
public class DataPowerController {

    @Autowired
    private DataPowerService dataPowerService;

    @Autowired
    private NCLoginUtils loginUtils;

    @Autowired
    private RedisUtils redisUtils;

    @ApiAnnotation(modularName = "数据权限管理", description = "查询数据权限列表")
    @PostMapping("/getList")
    public NCResult<OperateDataPower> getList(@RequestBody QueryVO<OperateDataPower> queryVO) {
        List<String> authList = getAdminAuth("dataPower");
        if (authList == null) {
            throw new NCException("", "权限不足！");
        }
        queryVO.setStringList(authList);
        Page page = PageHelper.startPage(queryVO.getPageNum(), queryVO.getPageSize());
        List<OperateDataPower> list = dataPowerService.getList(queryVO);
        return NCResult.ok(list, page.getTotal());
    }

    @ApiAnnotation(modularName = "数据权限管理", description = "添加数据权限")
    @PostMapping("/addDataPower")
    public NCResult addDataPower(@RequestBody OperateDataPower dataPower) {
        dataPowerService.addDataPower(dataPower);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "数据权限管理", description = "添加数据权限")
    @PostMapping("/editDataPower")
    public NCResult editDataPower(@RequestBody OperateDataPower dataPower) {
        List<String> authList = getAdminAuth("dataPower");
        if (authList == null || (!authList.isEmpty() && !authList.contains(dataPower.getDataPowerCode()))) {
            throw new NCException("", "权限不足！");
        }
        dataPowerService.editDataPower(dataPower);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "数据权限管理", description = "查询授权数据权限列表")
    @PostMapping("/getAuthList")
    public NCResult<OperateDataPowerAuth> getAuthList(@RequestBody QueryVO<OperateDataPower> queryVO) {
        List<String> authList = getAdminAuth("dataPower");
        if (authList == null || (!authList.isEmpty() && !authList.contains(queryVO.getData().getDataPowerCode()))) {
            throw new NCException("", "权限不足！");
        }
        return NCResult.ok(dataPowerService.getAuthList(queryVO));
    }

    @ApiAnnotation(modularName = "数据权限管理", description = "添加授权")
    @PostMapping("/addAuth")
    public NCResult addAuth(@RequestBody OperateDataPowerAuth auth) {
        List<String> authList = getAdminAuth("dataPower");
        if (authList == null || (!authList.isEmpty() && !authList.contains(auth.getDataPowerCode()))) {
            throw new NCException("", "权限不足！");
        }
        dataPowerService.addAuth(auth);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "数据权限管理", description = "编辑授权")
    @PostMapping("/editAuth")
    public NCResult editAuth(@RequestBody OperateDataPowerAuth auth) {
        List<String> authList = getAdminAuth("dataPower");
        if (authList == null || (!authList.isEmpty() && !authList.contains(auth.getDataPowerCode()))) {
            throw new NCException("", "权限不足！");
        }
        dataPowerService.editAuth(auth);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "数据权限管理", description = "删除授权")
    @PostMapping("/delAuth")
    public NCResult delAuth(@RequestBody OperateDataPowerAuth auth) {
        List<String> authList = getAdminAuth("dataPower");
        if (authList == null || (!authList.isEmpty() && !authList.contains(auth.getDataPowerCode()))) {
            throw new NCException("", "权限不足！");
        }
        dataPowerService.delAuth(auth);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "数据权限管理", description = "获取用户授权信息")
    @PostMapping("/getUserAuth")
    public NCResult<String> getUserAuth(@RequestBody DataPowerVO dataPowerVO) {
        return NCResult.ok(dataPowerService.getUserAuth(dataPowerVO));
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
