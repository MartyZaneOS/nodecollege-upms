package com.nodecollege.cloud.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nodecollege.cloud.client.utils.NCLoginUtils;
import com.nodecollege.cloud.common.annotation.ApiAnnotation;
import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.enums.ErrorEnum;
import com.nodecollege.cloud.common.model.*;
import com.nodecollege.cloud.common.model.po.*;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 租户成员管理
 *
 * @author LC
 * @date 2019/6/13 16:12
 */
@RestController
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private NCLoginUtils loginUtils;

    @ApiAnnotation(modularName = "成员管理", description = "获取所有成员信息")
    @PostMapping("/getMemberList")
    public NCResult<TenantMember> getMemberList(@RequestBody QueryVO<TenantMember> queryVO) {
        NCLoginUserVO loginMember = loginUtils.getMemberLoginInfo();
        NCUtils.nullOrEmptyThrow(loginMember, ErrorEnum.LOGIN_TIME_OUT);
        queryVO.getData().setTenantId(loginMember.getTenantId());

        List<TenantMember> list = new ArrayList<>();
        Long total = 0L;
        if (NCConstants.INT_NEGATIVE_1.equals(queryVO.getPageSize())) {
            list = memberService.list(queryVO);
            total = NCUtils.isNullOrEmpty(list) ? 0 : Long.parseLong(list.size() + "");
        } else {
            Page page = PageHelper.startPage(queryVO.getPageNum(), queryVO.getPageSize());
            if (queryVO.isSort()) {
                page.setOrderBy(queryVO.getSortKey() + " " + queryVO.getSortDirection());
            }
            list = memberService.list(queryVO);
            total = page.getTotal();
        }
        return NCResult.ok(list, total);
    }

    @ApiAnnotation(modularName = "成员管理", description = "邀请成员")
    @PostMapping("/inviteMember")
    public NCResult inviteMember(@RequestBody OperateUserInvite member) {
        NCLoginUserVO loginMember = loginUtils.getMemberLoginInfo();
        NCUtils.nullOrEmptyThrow(loginMember, ErrorEnum.LOGIN_TIME_OUT);
        member.setCreateUser(loginMember.getAccount());
        member.setTenantId(loginMember.getTenantId());
        member.setState(1);
        member = memberService.inviteMember(member);
        if (member != null) {
            return NCResult.ok("成员邀请成功！");
        }
        return NCResult.ok("已发出邀请，请等待！");
    }

    @ApiAnnotation(modularName = "成员管理", description = "邀请成员成功")
    @PostMapping("/inviteMemberSuccess")
    public NCResult inviteMemberSuccess(@RequestBody OperateUser user) {
        memberService.inviteMemberSuccess(user);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "成员管理", description = "更新成员信息")
    @PostMapping("/editMember")
    public NCResult<TenantMember> editMember(@RequestBody TenantMember user) {
        NCLoginUserVO loginMember = loginUtils.getMemberLoginInfo();
        NCUtils.nullOrEmptyThrow(loginMember, ErrorEnum.LOGIN_TIME_OUT);

        user.setTenantId(loginMember.getTenantId());
        memberService.updateMember(user, null);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "成员管理", description = "删除成员信息")
    @PostMapping("/delMember")
    public NCResult delMember(@RequestBody TenantMember user) {
        NCLoginUserVO loginMember = loginUtils.getMemberLoginInfo();
        NCUtils.nullOrEmptyThrow(loginMember, ErrorEnum.LOGIN_TIME_OUT);

        user.setTenantId(loginMember.getTenantId());
        memberService.delMember(user);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "成员管理", description = "重置密码")
    @PostMapping("/resetPwd")
    public NCResult resetPwd(@RequestBody TenantMember user) {
        NCLoginUserVO loginMember = loginUtils.getMemberLoginInfo();
        NCUtils.nullOrEmptyThrow(loginMember, ErrorEnum.LOGIN_TIME_OUT);

        user.setTenantId(loginMember.getTenantId());
        memberService.resetPwd(user);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "管理员管理", description = "获取成员列表")
    @PostMapping("/getMemberListByOrg")
    public NCResult<TenantMember> getMemberListByOrg(@RequestBody QueryVO<TenantMemberOrg> queryVO) {
        NCLoginUserVO loginMember = loginUtils.getMemberLoginInfo();
        NCUtils.nullOrEmptyThrow(loginMember, ErrorEnum.LOGIN_TIME_OUT);

        queryVO.getData().setTenantId(loginMember.getTenantId());
        List<TenantMember> list = new ArrayList<>();
        Long total = 0L;
        if (NCConstants.INT_NEGATIVE_1.equals(queryVO.getPageSize())) {
            list = memberService.getMemberListByOrg(queryVO);
            total = NCUtils.isNullOrEmpty(list) ? 0 : Long.parseLong(list.size() + "");
        } else {
            Page page = PageHelper.startPage(queryVO.getPageNum(), queryVO.getPageSize());
            if (queryVO.isSort()) {
                page.setOrderBy(queryVO.getSortKey() + " " + queryVO.getSortDirection());
            }
            list = memberService.getMemberListByOrg(queryVO);
            total = page.getTotal();
        }
        return NCResult.ok(list, total);
    }

    @ApiAnnotation(modularName = "管理员角色管理", description = "绑定成员机构")
    @PostMapping("/bindMemberOrg")
    public NCResult bindMemberOrg(@RequestBody BindVO bindVO) {
        NCLoginUserVO loginMember = loginUtils.getMemberLoginInfo();
        NCUtils.nullOrEmptyThrow(loginMember, ErrorEnum.LOGIN_TIME_OUT);

        bindVO.setTenantId(loginMember.getTenantId());
        memberService.bindMemberOrg(bindVO);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "成员机构角色管理", description = "根据角色机构获取成员列表")
    @PostMapping("/getMemberListByRoleOrg")
    public NCResult<TenantMember> getMemberListByRoleOrg(@RequestBody TenantMemberOrgRole queryVO) {
        NCLoginUserVO loginMember = loginUtils.getMemberLoginInfo();
        NCUtils.nullOrEmptyThrow(loginMember, ErrorEnum.LOGIN_TIME_OUT);

        queryVO.setTenantId(loginMember.getTenantId());
        List<TenantMember> userList = memberService.getMemberListByRoleOrg(queryVO);
        return NCResult.ok(userList);
    }

    @ApiAnnotation(modularName = "成员机构角色管理", description = "绑定成员机构角色")
    @PostMapping("/addMemberOrgRole")
    public NCResult addMemberOrgRole(@RequestBody TenantMemberOrgRole bindVO) {
        NCLoginUserVO loginMember = loginUtils.getMemberLoginInfo();
        NCUtils.nullOrEmptyThrow(loginMember, ErrorEnum.LOGIN_TIME_OUT);

        bindVO.setTenantId(loginMember.getTenantId());
        memberService.addMemberOrgRole(bindVO);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "成员机构角色管理", description = "解绑成员机构角色")
    @PostMapping("/delMemberOrgRole")
    public NCResult delMemberOrgRole(@RequestBody TenantMemberOrgRole bindVO) {
        NCLoginUserVO loginMember = loginUtils.getMemberLoginInfo();
        NCUtils.nullOrEmptyThrow(loginMember, ErrorEnum.LOGIN_TIME_OUT);

        bindVO.setTenantId(loginMember.getTenantId());
        memberService.delMemberOrgRole(bindVO);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "成员管理", description = "查询成员授权信息")
    @PostMapping("/getMemberPower")
    public NCResult<PowerVO> getMemberPower(@RequestBody TenantMember user) {
        NCLoginUserVO loginMember = loginUtils.getMemberLoginInfo();
        NCUtils.nullOrEmptyThrow(loginMember, ErrorEnum.LOGIN_TIME_OUT);

        user.setTenantId(loginMember.getTenantId());
        PowerVO powerVO = memberService.getMemberPower(user);
        return NCResult.ok(powerVO);
    }
}
