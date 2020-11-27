package com.nodecollege.cloud.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nodecollege.cloud.common.annotation.ApiAnnotation;
import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.model.BindVO;
import com.nodecollege.cloud.common.model.MenuVO;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateRole;
import com.nodecollege.cloud.common.model.po.OperateTenant;
import com.nodecollege.cloud.common.model.po.OperateTenantProduct;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LC
 * @date 2020/10/19 20:01
 */
@RestController
@RequestMapping("/tenant")
public class TenantController {

    @Autowired
    private TenantService tenantService;

    @ApiAnnotation(modularName = "租户管理", description = "获取租户列表")
    @PostMapping("/getTenantList")
    public NCResult<OperateTenant> getTenantList(@RequestBody QueryVO<OperateTenant> queryVO) {
        List<OperateTenant> list = new ArrayList<>();
        Long total = 0L;
        if (NCConstants.INT_NEGATIVE_1.equals(queryVO.getPageSize())) {
            list = tenantService.getTenantList(queryVO);
            total = NCUtils.isNullOrEmpty(list) ? 0 : Long.parseLong(list.size() + "");
        } else {
            Page page = PageHelper.startPage(queryVO.getPageNum(), queryVO.getPageSize());
            if (queryVO.isSort()) {
                page.setOrderBy(queryVO.getSortKey() + " " + queryVO.getSortDirection());
            }
            list = tenantService.getTenantList(queryVO);
            total = page.getTotal();
        }
        return NCResult.ok(list, total);
    }

    @ApiAnnotation(modularName = "租户管理", description = "获取租户信息")
    @PostMapping("/getTenantInfo")
    public NCResult<OperateTenant> getTenantInfo(@RequestBody OperateTenant query) {
        OperateTenant tenant = tenantService.getTenantInfo(query);
        return tenant != null ? NCResult.ok(tenant) : NCResult.error("-1", "租户不存在！");
    }

    @ApiAnnotation(modularName = "租户管理", description = "修改租户信息")
    @PostMapping("/editTenant")
    public NCResult editTenant(@RequestBody OperateTenant tenant) {
        tenantService.updateTenant(tenant);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "租户管理", description = "查询租户产品列表")
    @PostMapping("/getTenantProduct")
    public NCResult<OperateTenantProduct> getTenantProduct(@RequestBody OperateTenantProduct query) {
        return NCResult.ok(tenantService.getTenantProduct(query));
    }

    @ApiAnnotation(modularName = "租户管理", description = "绑定租户产品")
    @PostMapping("/bindTenantProduct")
    public NCResult bindTenantProduct(@RequestBody BindVO bindVO) {
        tenantService.bindTenantProduct(bindVO);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "租户管理", description = "查询租户所有菜单")
    @PostMapping("/getTenantMenu")
    public NCResult<MenuVO> getTenantMenu(@RequestBody OperateTenant tenant) {
        return NCResult.ok(tenantService.getTenantMenu(tenant));
    }

    @ApiAnnotation(modularName = "租户管理", description = "查询租户所有角色")
    @PostMapping("/getTenantRole")
    public NCResult<OperateRole> getTenantRole(@RequestBody OperateTenant tenant) {
        return NCResult.ok(tenantService.getTenantRole(tenant));
    }
}
