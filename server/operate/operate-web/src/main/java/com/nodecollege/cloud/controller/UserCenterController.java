package com.nodecollege.cloud.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nodecollege.cloud.client.utils.NCLoginUtils;
import com.nodecollege.cloud.common.annotation.ApiAnnotation;
import com.nodecollege.cloud.common.constants.HeaderConstants;
import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.enums.ErrorEnum;
import com.nodecollege.cloud.common.model.NCLoginUserVO;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateAdmin;
import com.nodecollege.cloud.common.model.po.OperateTenant;
import com.nodecollege.cloud.common.model.po.OperateUser;
import com.nodecollege.cloud.common.model.po.OperateUserTenant;
import com.nodecollege.cloud.common.utils.Base64Util;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.service.TenantService;
import com.nodecollege.cloud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户中心
 *
 * @author LC
 * @date 2019/12/7 10:26
 */
@RestController
@RequestMapping("/userCenter")
public class UserCenterController {

    @Autowired
    private UserService userService;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private NCLoginUtils loginUtils;

    /**
     * 注册企业（租户）
     *
     * @param operateTenant
     * @return
     */
    @ApiAnnotation(modularName = "用户中心", description = "注册企业（租户）")
    @PostMapping("/registerTenant")
    public NCResult registerTenant(@RequestBody OperateTenant operateTenant) {
        NCLoginUserVO loginUser = loginUtils.getUserLoginInfo();
        NCUtils.nullOrEmptyThrow(loginUser, ErrorEnum.LOGIN_TIME_OUT);

        operateTenant.setCreateUserId(loginUser.getLoginId());
        operateTenant.setTenantId(null);
        tenantService.addTenant(operateTenant);
        return NCResult.ok("添加租户成功");
    }

    @ApiAnnotation(modularName = "用户中心", description = "查询个人信息")
    @PostMapping("/getUserInfo")
    public NCResult<OperateUser> getUserInfo() {
        NCLoginUserVO loginUser = loginUtils.getUserLoginInfo();
        NCUtils.nullOrEmptyThrow(loginUser, ErrorEnum.LOGIN_TIME_OUT);

        return NCResult.ok(userService.getUserByUserId(loginUser.getLoginId()));
    }

    @ApiAnnotation(modularName = "用户中心", description = "修改个人信息")
    @PostMapping("/updateUserInfo")
    public NCResult<NCLoginUserVO> updateUserInfo(@RequestBody OperateUser operateUser, HttpServletResponse response) {
        NCLoginUserVO loginUser = loginUtils.getUserLoginInfo();
        NCUtils.nullOrEmptyThrow(loginUser, ErrorEnum.LOGIN_TIME_OUT);

        operateUser.setUserId(loginUser.getLoginId());
        loginUser = userService.updateUser(operateUser, loginUser.getAccessToken());
        Cookie accessTokenCookie = new Cookie(HeaderConstants.USER_ACCESS_TOKEN, loginUser.getAccessToken());
        accessTokenCookie.setMaxAge(loginUser.getExpire().intValue());
        accessTokenCookie.setPath("/");
        Cookie uuidCookie = new Cookie(HeaderConstants.USER_UUID, loginUser.getUuid());
        uuidCookie.setMaxAge(loginUser.getExpire().intValue());
        uuidCookie.setPath("/");
        response.addCookie(accessTokenCookie);
        response.addCookie(uuidCookie);
        return NCResult.ok(loginUser);
    }

    /**
     * 查询个人的所有租户信息
     *
     * @param queryVO
     * @return
     */
    @ApiAnnotation(modularName = "用户中心", description = "查询个人的所有租户信息")
    @PostMapping("/getTenantList")
    public NCResult<OperateTenant> getTenantList(@RequestBody QueryVO queryVO) {
        NCLoginUserVO loginUser = loginUtils.getUserLoginInfo();
        NCUtils.nullOrEmptyThrow(loginUser, ErrorEnum.LOGIN_TIME_OUT);

        OperateUserTenant userTenant = new OperateUserTenant();
        userTenant.setUserId(loginUser.getLoginId());
        List<OperateTenant> list = new ArrayList<>();
        Long total = 0L;
        if (NCConstants.INT_NEGATIVE_1.equals(queryVO.getPageSize())) {
            list = tenantService.getTenantListByUserId(userTenant);
            total = NCUtils.isNullOrEmpty(list) ? 0 : Long.parseLong(list.size() + "");
        } else {
            Page page = PageHelper.startPage(queryVO.getPageNum(), queryVO.getPageSize());
            if (queryVO.isSort()) {
                page.setOrderBy(queryVO.getSortKey() + " " + queryVO.getSortDirection());
            }
            list = tenantService.getTenantListByUserId(userTenant);
            total = page.getTotal();
        }
        return NCResult.ok(list, total);
    }

    @ApiAnnotation(modularName = "用户中心", description = "设置并切换默认登陆企业")
    @PostMapping("/setDefaultTenant")
    public NCResult setDefaultTenant(@RequestBody OperateUserTenant loginUser) {
        NCLoginUserVO login = loginUtils.getUserLoginInfo();
        NCUtils.nullOrEmptyThrow(loginUser, ErrorEnum.LOGIN_TIME_OUT);

        loginUser.setUserId(login.getLoginId());
        userService.changeDefaultTenant(loginUser);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "用户中心", description = "上传头像")
    @PostMapping("/uploadAvatar")
    public NCResult<OperateUser> uploadAvatar(@RequestBody JSONObject object) {
        NCLoginUserVO loginUser = loginUtils.getUserLoginInfo();
        NCUtils.nullOrEmptyThrow(loginUser, ErrorEnum.LOGIN_TIME_OUT);

        MultipartFile avatarFile = Base64Util.base64ToMultipart(object.getString("avatar"));
        OperateUser user = userService.uploadAvatar(avatarFile, loginUser.getLoginId());
        return NCResult.ok(user);
    }

    @ApiAnnotation(modularName = "管理员中心", description = "变更默认机构角色选项")
    @PostMapping("/changeUserDefaultOption")
    public NCResult<NCLoginUserVO> changeUserDefaultOption(@RequestBody OperateUser user, HttpServletResponse response){
        // 获取当前登录人信息
        NCLoginUserVO loginUser = loginUtils.getUserLoginInfo();
        NCUtils.nullOrEmptyThrow(loginUser, ErrorEnum.LOGIN_TIME_OUT);
        user.setUserId(loginUser.getLoginId());
        loginUser = userService.changeDefaultOption(loginUser.getAccessToken(), user);
        Cookie accessToken = new Cookie(HeaderConstants.USER_ACCESS_TOKEN, loginUser.getAccessToken());
        accessToken.setMaxAge(loginUser.getExpire());
        accessToken.setPath("/");
        Cookie uuid = new Cookie(HeaderConstants.USER_UUID, loginUser.getUuid());
        uuid.setMaxAge(loginUser.getExpire());
        uuid.setPath("/");
        response.addCookie(accessToken);
        response.addCookie(uuid);
        return NCResult.ok(loginUser);
    }

}
