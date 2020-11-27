package com.nodecollege.cloud.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nodecollege.cloud.common.annotation.ApiAnnotation;
import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.model.BindVO;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.PowerVO;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateAdmin;
import com.nodecollege.cloud.common.model.po.OperateAdminOrg;
import com.nodecollege.cloud.common.model.po.OperateAdminOrgRole;
import com.nodecollege.cloud.common.model.vo.LoginVO;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LC
 * @date 2019/11/27 20:55
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @ApiAnnotation(modularName = "管理员管理", description = "获取管理员列表")
    @PostMapping("/getAdminList")
    public NCResult<OperateAdmin> getAdminList(@RequestBody QueryVO<OperateAdmin> queryVO) {
        //  登录认证
        return adminService.getAdminList(queryVO);
    }

    @ApiAnnotation(modularName = "管理员管理", description = "新增管理员信息")
    @PostMapping("/addAdmin")
    public NCResult addAdmin(@RequestBody LoginVO loginVO) {
        loginVO.setSource(1);
        adminService.addAdmin(loginVO);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "管理员管理", description = "更新管理员")
    @PostMapping("/updateAdmin")
    public NCResult updateAdmin(@RequestBody OperateAdmin admin) {
        adminService.updateAdmin(admin);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "管理员管理", description = "删除管理员")
    @PostMapping("/delAdmin")
    public NCResult delAdmin(@RequestBody OperateAdmin admin) {
        adminService.delAdmin(admin.getAdminId());
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "管理员管理", description = "锁定/解锁管理员")
    @PostMapping("/lockAdmin")
    public NCResult lockAdmin(@RequestBody OperateAdmin admin) {
        adminService.lockAdmin(admin);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "管理员管理", description = "重置管理员密码")
    @PostMapping("/resetPwd")
    public NCResult resetPwd(@RequestBody OperateAdmin admin) {
        adminService.resetPwd(admin);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "管理员管理", description = "获取管理员列表")
    @PostMapping("/getAdminListByOrg")
    public NCResult<OperateAdmin> getAdminListByOrg(@RequestBody QueryVO<OperateAdminOrg> queryVO) {
        List<OperateAdmin> list = new ArrayList<>();
        Long total = 0L;
        if (NCConstants.INT_NEGATIVE_1.equals(queryVO.getPageSize())) {
            list = adminService.getAdminListByOrg(queryVO);
            total = NCUtils.isNullOrEmpty(list) ? 0 : Long.parseLong(list.size() + "");
        } else {
            Page page = PageHelper.startPage(queryVO.getPageNum(), queryVO.getPageSize());
            if (queryVO.isSort()) {
                page.setOrderBy(queryVO.getSortKey() + " " + queryVO.getSortDirection());
            }
            list = adminService.getAdminListByOrg(queryVO);
            total = page.getTotal();
        }
        return NCResult.ok(list, total);
    }

    @ApiAnnotation(modularName = "管理员角色管理", description = "绑定管理员机构")
    @PostMapping("/bindAdminOrg")
    public NCResult bindAdminOrg(@RequestBody BindVO bindVO) {
        adminService.bindAdminOrg(bindVO);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "管理员管理", description = "获取管理员列表")
    @PostMapping("/getAdminListByRoleOrg")
    public NCResult<OperateAdmin> getAdminListByRoleOrg(@RequestBody OperateAdminOrgRole queryVO) {
        return NCResult.ok(adminService.getAdminListByRoleOrg(queryVO));
    }

    @ApiAnnotation(modularName = "管理员角色管理", description = "绑定管理员机构角色")
    @PostMapping("/addAdminOrgRole")
    public NCResult addAdminOrgRole(@RequestBody OperateAdminOrgRole bindVO) {
        adminService.addAdminOrgRole(bindVO);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "管理员角色管理", description = "解绑管理员机构角色")
    @PostMapping("/delAdminOrgRole")
    public NCResult delAdminOrgRole(@RequestBody OperateAdminOrgRole bindVO) {
        adminService.delAdminOrgRole(bindVO);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "管理员管理", description = "查询管理员授权信息")
    @PostMapping("/getAdminPower")
    public NCResult<PowerVO> getAdminPower(@RequestBody OperateAdmin admin) {
        PowerVO powerVO = adminService.getAdminPower(admin);
        return NCResult.ok(powerVO);
    }
}
