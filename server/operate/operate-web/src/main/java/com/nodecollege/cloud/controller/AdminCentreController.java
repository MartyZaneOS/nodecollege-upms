package com.nodecollege.cloud.controller;

import com.nodecollege.cloud.client.utils.NCLoginUtils;
import com.nodecollege.cloud.common.annotation.ApiAnnotation;
import com.nodecollege.cloud.common.constants.HeaderConstants;
import com.nodecollege.cloud.common.enums.ErrorEnum;
import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.NCLoginUserVO;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.po.OperateAdmin;
import com.nodecollege.cloud.common.model.vo.LoginVO;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * 管理员中心
 *
 * @author LC
 * @date 2019/12/2 20:47
 */
@RestController
@RequestMapping("/admin/centre")
public class AdminCentreController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private NCLoginUtils loginUtils;

    /**
     * 修改密码
     *
     * @param loginVO
     */
    @ApiAnnotation(modularName = "管理员中心", description = "修改密码")
    @PostMapping("/updatePassword")
    public NCResult updatePassword(@RequestBody LoginVO loginVO) {
        // 获取当前登录人信息
        NCLoginUserVO loginAdmin = loginUtils.getAdminLoginInfo();
        NCUtils.nullOrEmptyThrow(loginAdmin, ErrorEnum.LOGIN_TIME_OUT);
        adminService.updateAdminPassword(loginAdmin.getAccount(), loginVO.getPassword(), loginVO.getNewPassword(), loginVO.getImageCert());
        return NCResult.ok();
    }

    /**
     * 修改个人信息
     *
     * @param admin
     * @return
     */
    @ApiAnnotation(modularName = "管理员中心", description = "修改个人信息")
    @PostMapping("/updateInfo")
    public NCResult updateInfo(@RequestBody OperateAdmin admin) {
        // 获取当前登录人信息
        NCLoginUserVO loginAdmin = loginUtils.getAdminLoginInfo();
        NCUtils.nullOrEmptyThrow(loginAdmin, ErrorEnum.LOGIN_TIME_OUT);
        admin.setAdminId(loginAdmin.getLoginId());
        adminService.updateAdmin(admin);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "管理员中心", description = "变更默认机构角色选项")
    @PostMapping("/changeDefaultOption")
    public NCResult<NCLoginUserVO> changeDefaultOption(@RequestBody OperateAdmin admin, HttpServletResponse response){
        // 获取当前登录人信息
        NCLoginUserVO loginAdmin = loginUtils.getAdminLoginInfo();
        NCUtils.nullOrEmptyThrow(loginAdmin, ErrorEnum.LOGIN_TIME_OUT);
        admin.setAdminId(loginAdmin.getLoginId());
        loginAdmin = adminService.changeDefaultOption(loginAdmin.getAccessToken(), admin);
        Cookie accessToken = new Cookie(HeaderConstants.ADMIN_ACCESS_TOKEN, loginAdmin.getAccessToken());
        accessToken.setMaxAge(loginAdmin.getExpire());
        accessToken.setPath("/");
        Cookie uuid = new Cookie(HeaderConstants.ADMIN_UUID, loginAdmin.getUuid());
        uuid.setMaxAge(loginAdmin.getExpire());
        uuid.setPath("/");
        response.addCookie(accessToken);
        response.addCookie(uuid);
        return NCResult.ok(loginAdmin);
    }
}
