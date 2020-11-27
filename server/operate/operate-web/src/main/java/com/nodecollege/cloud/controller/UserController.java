package com.nodecollege.cloud.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nodecollege.cloud.common.annotation.ApiAnnotation;
import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.model.BindVO;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.PowerVO;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateUser;
import com.nodecollege.cloud.common.model.po.OperateUserOrg;
import com.nodecollege.cloud.common.model.po.OperateUserOrgRole;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 运维后端用户管理
 *
 * @author LC
 * @date 2019/6/13 16:12
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @ApiAnnotation(modularName = "用户管理", description = "获取所有用户信息")
    @PostMapping("/getUserList")
    public NCResult<OperateUser> getUserList(@RequestBody QueryVO<OperateUser> query) {
        // 设置数据权限
        return userService.list(query);
    }

    @ApiAnnotation(modularName = "用户管理", description = "更新用户信息")
    @PostMapping("/editUser")
    public NCResult<OperateUser> editUser(@RequestBody OperateUser user) {
        userService.updateUser(user, null);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "用户管理", description = "删除用户信息")
    @PostMapping("/delUser")
    public NCResult delUser(@RequestBody OperateUser user) {
        userService.delUser(user.getUserId());
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "用户管理", description = "重置密码")
    @PostMapping("/resetUserPwd")
    public NCResult resetUserPwd(@RequestBody OperateUser user) {
        userService.resetPwd(user);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "管理员管理", description = "获取用户列表")
    @PostMapping("/getUserListByOrg")
    public NCResult<OperateUser> getUserListByOrg(@RequestBody QueryVO<OperateUserOrg> queryVO) {
        List<OperateUser> list = new ArrayList<>();
        Long total = 0L;
        if (NCConstants.INT_NEGATIVE_1.equals(queryVO.getPageSize())) {
            list = userService.getUserListByOrg(queryVO);
            total = NCUtils.isNullOrEmpty(list) ? 0 : Long.parseLong(list.size() + "");
        } else {
            Page page = PageHelper.startPage(queryVO.getPageNum(), queryVO.getPageSize());
            if (queryVO.isSort()) {
                page.setOrderBy(queryVO.getSortKey() + " " + queryVO.getSortDirection());
            }
            list = userService.getUserListByOrg(queryVO);
            total = page.getTotal();
        }
        return NCResult.ok(list, total);
    }

    @ApiAnnotation(modularName = "管理员角色管理", description = "绑定用户机构")
    @PostMapping("/bindUserOrg")
    public NCResult bindUserOrg(@RequestBody BindVO bindVO) {
        userService.bindUserOrg(bindVO);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "管理员管理", description = "获取管理员列表")
    @PostMapping("/getUserListByRoleOrg")
    public NCResult<OperateUser> getUserListByRoleOrg(@RequestBody OperateUserOrgRole queryVO) {
        List<OperateUser> userList = userService.getUserListByRoleOrg(queryVO);
        return NCResult.ok(userList);
    }

    @ApiAnnotation(modularName = "管理员角色管理", description = "绑定管理员机构角色")
    @PostMapping("/addUserOrgRole")
    public NCResult addUserOrgRole(@RequestBody OperateUserOrgRole bindVO) {
        userService.addUserOrgRole(bindVO);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "管理员角色管理", description = "解绑管理员机构角色")
    @PostMapping("/delUserOrgRole")
    public NCResult delUserOrgRole(@RequestBody OperateUserOrgRole bindVO) {
        userService.delUserOrgRole(bindVO);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "用户内部接口", description = "获取用户信息列表！", accessSource = 2)
    @PostMapping("/getUserListByQuery")
    public NCResult<OperateUser> getUserListByQuery(@RequestBody QueryVO queryVO) {
        return NCResult.ok(userService.getUserListByQuery(queryVO));
    }

    @ApiAnnotation(modularName = "用户内部接口", description = "根据用户id获取用户信息！", accessSource = 2)
    @PostMapping("/getUserByUserId")
    public NCResult<OperateUser> getUserByUserId(@RequestBody Long userId) {
        return NCResult.ok(userService.getUserByUserId(userId));
    }

    @ApiAnnotation(modularName = "用户管理", description = "查询用户授权信息")
    @PostMapping("/getUserPower")
    public NCResult<PowerVO> getUserPower(@RequestBody OperateUser user) {
        PowerVO powerVO = userService.getUserPower(user);
        return NCResult.ok(powerVO);
    }

    @ApiAnnotation(modularName = "我的好友", description = "根据昵称查询用户信息！")
    @PostMapping("/getUserListByNickname")
    public NCResult<OperateUser> getUserListByNickname(@RequestBody QueryVO<OperateUser> queryVO) {
        return userService.getUserListByNickname(queryVO);
    }
}
