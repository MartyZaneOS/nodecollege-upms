package com.nodecollege.cloud.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nodecollege.cloud.client.utils.NCLoginUtils;
import com.nodecollege.cloud.common.annotation.ApiAnnotation;
import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.enums.ErrorEnum;
import com.nodecollege.cloud.common.model.BindVO;
import com.nodecollege.cloud.common.model.NCLoginUserVO;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.TenantMemberOrgRole;
import com.nodecollege.cloud.common.model.po.TenantRole;
import com.nodecollege.cloud.common.model.po.TenantRoleMenu;
import com.nodecollege.cloud.common.model.po.TenantRoleOrg;
import com.nodecollege.cloud.common.utils.NCUtils;
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
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private NCLoginUtils loginUtils;

    @ApiAnnotation(modularName = "角色信息", description = "查询角色列表")
    @PostMapping("/getRoleList")
    public NCResult<TenantRole> getRoleList(@RequestBody QueryVO<TenantRole> queryVO) {
        NCLoginUserVO loginMember = loginUtils.getMemberLoginInfo();
        NCUtils.nullOrEmptyThrow(loginMember, ErrorEnum.LOGIN_TIME_OUT);
        queryVO.getData().setTenantId(loginMember.getTenantId());
        return NCResult.ok(roleService.getRoleList(queryVO));
    }

    @ApiAnnotation(modularName = "角色信息", description = "添加角色")
    @PostMapping("/addRole")
    public NCResult addRole(@RequestBody TenantRole role) {
        NCLoginUserVO loginMember = loginUtils.getMemberLoginInfo();
        NCUtils.nullOrEmptyThrow(loginMember, ErrorEnum.LOGIN_TIME_OUT);
        role.setTenantId(loginMember.getTenantId());
        role.setCreateUser(loginMember.getAccount());

        roleService.addRole(role);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "角色信息", description = "编辑角色")
    @PostMapping("/editRole")
    public NCResult editRole(@RequestBody TenantRole role) {
        NCLoginUserVO loginMember = loginUtils.getMemberLoginInfo();
        NCUtils.nullOrEmptyThrow(loginMember, ErrorEnum.LOGIN_TIME_OUT);
        role.setTenantId(loginMember.getTenantId());
        role.setUpdateUser(loginMember.getAccount());

        roleService.editRole(role);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "预制角色信息", description = "删除角色")
    @PostMapping("/delRole")
    public NCResult delRole(@RequestBody TenantRole role) {
        NCLoginUserVO loginMember = loginUtils.getMemberLoginInfo();
        NCUtils.nullOrEmptyThrow(loginMember, ErrorEnum.LOGIN_TIME_OUT);
        role.setTenantId(loginMember.getTenantId());
        role.setUpdateUser(loginMember.getAccount());

        roleService.delRole(role);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "角色菜单信息", description = "查询角色菜单")
    @PostMapping("/getRoleMenuList")
    public NCResult<TenantRoleMenu> getRoleMenuList(@RequestBody QueryVO<TenantRoleMenu> queryVO) {
        NCLoginUserVO loginMember = loginUtils.getMemberLoginInfo();
        NCUtils.nullOrEmptyThrow(loginMember, ErrorEnum.LOGIN_TIME_OUT);
        queryVO.getData().setTenantId(loginMember.getTenantId());
        List<TenantRoleMenu> list = new ArrayList<>();
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
        NCLoginUserVO loginMember = loginUtils.getMemberLoginInfo();
        NCUtils.nullOrEmptyThrow(loginMember, ErrorEnum.LOGIN_TIME_OUT);
        bindVO.setTenantId(loginMember.getTenantId());
        roleService.bindRoleMenu(bindVO);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "组织机构信息", description = "查询组织机构列表")
    @PostMapping("/getRoleListByOrg")
    public NCResult<TenantRole> getRoleListByOrg(@RequestBody QueryVO<TenantRoleOrg> queryVO) {
        NCLoginUserVO loginMember = loginUtils.getMemberLoginInfo();
        NCUtils.nullOrEmptyThrow(loginMember, ErrorEnum.LOGIN_TIME_OUT);
        queryVO.getData().setTenantId(loginMember.getTenantId());
        return NCResult.ok(roleService.getRoleListByOrg(queryVO));
    }

    @ApiAnnotation(modularName = "组织机构信息", description = "绑定角色组织机构")
    @PostMapping("/bindRoleOrg")
    public NCResult bindRoleOrg(@RequestBody BindVO bindVO) {
        NCLoginUserVO loginMember = loginUtils.getMemberLoginInfo();
        NCUtils.nullOrEmptyThrow(loginMember, ErrorEnum.LOGIN_TIME_OUT);
        bindVO.setTenantId(loginMember.getTenantId());
        roleService.bindRoleOrg(bindVO);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "组织机构信息", description = "查询组织机构列表")
    @PostMapping("/getRoleListByMember")
    public NCResult<TenantRole> getRoleListByMember(@RequestBody QueryVO<TenantMemberOrgRole> queryVO) {
        NCLoginUserVO loginMember = loginUtils.getMemberLoginInfo();
        NCUtils.nullOrEmptyThrow(loginMember, ErrorEnum.LOGIN_TIME_OUT);
        queryVO.getData().setTenantId(loginMember.getTenantId());
        List<TenantRole> list = roleService.getRoleListByMember(queryVO);
        return NCResult.ok(list);
    }
}
