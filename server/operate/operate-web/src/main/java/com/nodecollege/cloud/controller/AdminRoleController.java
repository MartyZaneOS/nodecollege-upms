package com.nodecollege.cloud.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nodecollege.cloud.common.annotation.ApiAnnotation;
import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.model.BindVO;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateAdminOrgRole;
import com.nodecollege.cloud.common.model.po.OperateRole;
import com.nodecollege.cloud.common.model.po.OperateRoleMenu;
import com.nodecollege.cloud.common.model.po.OperateRoleOrg;
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
@RequestMapping("/admin/role")
public class AdminRoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleOrgService roleOrgService;

    @ApiAnnotation(modularName = "预制角色信息", description = "查询角色列表")
    @PostMapping("/getRoleList")
    public NCResult<OperateRole> getRoleList(@RequestBody QueryVO<OperateRole> queryVO) {
        queryVO.getData().setRoleUsage(0);
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

    @ApiAnnotation(modularName = "预制角色信息", description = "添加角色")
    @PostMapping("/addRole")
    public NCResult addRole(@RequestBody OperateRole role) {
        role.setRoleUsage(0);
        roleService.addRole(role);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "预制角色信息", description = "编辑角色")
    @PostMapping("/editRole")
    public NCResult editRole(@RequestBody OperateRole role) {
        role.setRoleUsage(0);
        roleService.editRole(role);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "预制角色信息", description = "删除角色")
    @PostMapping("/delRole")
    public NCResult delRole(@RequestBody OperateRole role) {
        role.setRoleUsage(0);
        roleService.delRole(role);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "预制角色菜单信息", description = "查询角色菜单")
    @PostMapping("/getRoleMenuList")
    public NCResult<OperateRoleMenu> getRoleMenuList(@RequestBody QueryVO<OperateRoleMenu> queryVO) {
        queryVO.getData().setRoleMenuUsage(0);
        List<OperateRoleMenu> list = new ArrayList<>();
        Long total = 0L;
        if (NCConstants.INT_NEGATIVE_1.equals(queryVO.getPageSize())) {
            list = roleService.getRoleMenuList(queryVO);
            total = NCUtils.isNullOrEmpty(list) ? 0 : Long.parseLong(list.size() + "");
        } else {
            Page page = PageHelper.startPage(queryVO.getPageNum(), queryVO.getPageSize());
            if (queryVO.isSort()) {
                page.setOrderBy(queryVO.getSortKey() + " " + queryVO.getSortDirection());
            }
            list = roleService.getRoleMenuList(queryVO);
            total = page.getTotal();
        }
        return NCResult.ok(list, total);
    }

    @ApiAnnotation(modularName = "预制角色信息", description = "绑定角色菜单")
    @PostMapping("/bindRoleMenu")
    public NCResult bindRoleMenu(@RequestBody BindVO bindVO) {
        bindVO.setBindUsage(0);
        roleService.bindRoleMenu(bindVO);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "组织机构信息", description = "查询组织机构列表")
    @PostMapping("/getRoleListByOrg")
    public NCResult<OperateRole> getRoleListByOrg(@RequestBody QueryVO<OperateRoleOrg> queryVO) {
        queryVO.getData().setRoleOrgUsage(0);
        return NCResult.ok(roleService.getRoleListByOrg(queryVO));
    }

    @ApiAnnotation(modularName = "组织机构信息", description = "绑定角色组织机构")
    @PostMapping("/bindRoleOrg")
    public NCResult bindRoleOrg(@RequestBody BindVO bindVO) {
        bindVO.setBindUsage(0);
        roleOrgService.bindRoleOrg(bindVO);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "组织机构信息", description = "查询组织机构列表")
    @PostMapping("/getRoleListByAdmin")
    public NCResult<OperateRole> getRoleListByAdmin(@RequestBody QueryVO<OperateAdminOrgRole> queryVO) {
        List<OperateRole> list = roleService.getRoleListByAdmin(queryVO);
        return NCResult.ok(list);
    }
}
