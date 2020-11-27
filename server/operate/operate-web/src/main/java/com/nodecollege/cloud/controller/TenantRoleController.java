package com.nodecollege.cloud.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nodecollege.cloud.common.annotation.ApiAnnotation;
import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.model.BindVO;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateRole;
import com.nodecollege.cloud.common.model.po.OperateRoleMenu;
import com.nodecollege.cloud.common.model.po.OperateRoleOrg;
import com.nodecollege.cloud.common.model.po.OperateUserOrgRole;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.service.RoleOrgService;
import com.nodecollege.cloud.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LC
 * @date 2020/8/31 20:42
 */
@RestController
@RequestMapping("/tenant/role")
public class TenantRoleController {

    @Autowired
    private RoleService roleService;

    @ApiAnnotation(modularName = "租户预制角色信息", description = "查询角色列表")
    @PostMapping("/getTenantRoleList")
    public NCResult<OperateRole> getTenantRoleList(@RequestBody QueryVO<OperateRole> queryVO) {
        queryVO.getData().setRoleUsage(2);
        List<OperateRole> list = new ArrayList<>();
        Long total = 0L;
        if (NCConstants.INT_NEGATIVE_1.equals(queryVO.getPageSize())) {
            list = roleService.getRoleList(queryVO);
            total = NCUtils.isNullOrEmpty(list) ? 0 : Long.parseLong(list.size() + "");
        } else {
            Page page = PageHelper.startPage(queryVO.getPageNum(), queryVO.getPageSize());
            if (queryVO.isSort()) {
                page.setOrderBy(queryVO.getSortKey() + " " + queryVO.getSortDirection());
            }
            list = roleService.getRoleList(queryVO);
            total = page.getTotal();
        }
        return NCResult.ok(list, total);
    }

    @ApiAnnotation(modularName = "租户预制角色信息", description = "添加角色")
    @PostMapping("/addTenantRole")
    public NCResult addTenantRole(@RequestBody OperateRole role) {
        role.setRoleUsage(2);
        roleService.addRole(role);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "租户预制角色信息", description = "编辑角色")
    @PostMapping("/editTenantRole")
    public NCResult editTenantRole(@RequestBody OperateRole role) {
        role.setRoleUsage(2);
        roleService.editRole(role);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "租户预制角色信息", description = "删除角色")
    @PostMapping("/delTenantRole")
    public NCResult delTenantRole(@RequestBody OperateRole role) {
        role.setRoleUsage(2);
        roleService.delRole(role);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "租户预制角色信息", description = "查询角色菜单")
    @PostMapping("/getTenantRoleMenuList")
    public NCResult<OperateRoleMenu> getTenantRoleMenuList(@RequestBody QueryVO<OperateRoleMenu> queryVO) {
        queryVO.getData().setRoleMenuUsage(2);
        return NCResult.ok(roleService.getRoleMenuList(queryVO));
    }

    @ApiAnnotation(modularName = "租户预制角色信息", description = "绑定角色菜单")
    @PostMapping("/bindTenantRoleMenu")
    public NCResult bindRoleMenu(@RequestBody BindVO bindVO) {
        bindVO.setBindUsage(2);
        roleService.bindRoleMenu(bindVO);
        return NCResult.ok();
    }
}
