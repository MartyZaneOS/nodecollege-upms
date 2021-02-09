package com.nodecollege.cloud.controller;

import com.nodecollege.cloud.client.utils.NCLoginUtils;
import com.nodecollege.cloud.common.annotation.ApiAnnotation;
import com.nodecollege.cloud.common.constants.HeaderConstants;
import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.NCLoginUserVO;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.vo.LoginVO;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.service.AdminLoginService;
import com.nodecollege.cloud.service.AdminService;
import com.nodecollege.cloud.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author LC
 * @date 2019/12/1 12:10
 */
@RestController
@RequestMapping("/admin")
public class AdminLoginController {

    @Autowired
    private AdminLoginService adminLoginService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private NCLoginUtils loginUtils;

    @Autowired
    private CommonService commonService;

    /**
     * 管理员登陆
     *
     * @param loginVO
     */
    @ApiAnnotation(modularName = "管理员登陆", description = "管理员登陆")
    @PostMapping("/login")
    public NCResult<NCLoginUserVO> login(@RequestBody LoginVO loginVO, HttpServletRequest request, HttpServletResponse response) {
        String imageCertSessionId = NCUtils.getCookieValue(HeaderConstants.IMAGE_CERT_SESSION_ID, request);
        if (!commonService.checkImageCert(imageCertSessionId, loginVO.getImageCert())) {
            throw new NCException("-1", "图片验证码错误，请刷新后重试！");
        }

        loginVO.setPassword(commonService.rsaDecrypt(loginVO.getRsaTag(), loginVO.getPassword()));
        NCLoginUserVO loginAdmin = adminLoginService.login(loginVO);
        Cookie accessToken = new Cookie(HeaderConstants.ADMIN_ACCESS_TOKEN, loginAdmin.getAccessToken());
        accessToken.setPath("/");
        Cookie uuid = new Cookie(HeaderConstants.ADMIN_UUID, loginAdmin.getUuid());
        uuid.setPath("/");
        response.addCookie(accessToken);
        response.addCookie(uuid);
        return NCResult.ok(loginAdmin);
    }

    @ApiAnnotation(modularName = "管理员登陆", description = "根据token获取用户信息")
    @PostMapping("/getAdminInfoByToken")
    public NCResult<NCLoginUserVO> getAdminInfoByToken(@RequestBody LoginVO loginVO, HttpServletResponse response) {
        NCLoginUserVO loginAdmin = adminLoginService.getAdminInfoByToken(loginVO);
        Cookie accessToken = null;
        Cookie uuid = null;
        if (loginAdmin != null) {
            accessToken = new Cookie(HeaderConstants.ADMIN_ACCESS_TOKEN, loginAdmin.getAccessToken());
            uuid = new Cookie(HeaderConstants.ADMIN_UUID, loginAdmin.getUuid());
        } else {
            accessToken = new Cookie(HeaderConstants.ADMIN_ACCESS_TOKEN, "");
            uuid = new Cookie(HeaderConstants.ADMIN_UUID, "");
            accessToken.setMaxAge(0);
            uuid.setMaxAge(0);
        }
        accessToken.setPath("/");
        uuid.setPath("/");
        response.addCookie(accessToken);
        response.addCookie(uuid);
        if (loginAdmin != null) {
            return NCResult.ok(loginAdmin);
        }
        return NCResult.error("-1", "管理员token已失效！");
    }

    /**
     * 退出登陆
     */
    @ApiAnnotation(modularName = "管理员登陆", description = "退出登陆")
    @RequestMapping("/logout")
    public NCResult logout(HttpServletResponse response) {
        NCLoginUserVO loginAdmin = loginUtils.getAdminLoginInfo();
        if (loginAdmin == null) {
            return NCResult.ok();
        }
        adminLoginService.logout(loginAdmin.getAccessToken());
        Cookie accessToken = new Cookie(HeaderConstants.ADMIN_ACCESS_TOKEN, null);
        accessToken.setMaxAge(0);
        accessToken.setPath("/");
        Cookie uuid = new Cookie(HeaderConstants.ADMIN_UUID, null);
        uuid.setMaxAge(0);
        uuid.setPath("/");
        response.addCookie(accessToken);
        response.addCookie(uuid);
        return NCResult.ok();
    }


    /**
     * 未登陆情况下重新设置密码
     *
     * @param loginVO
     * @return
     */
    @ApiAnnotation(modularName = "用户登陆", description = "未登陆情况下重新设置密码")
    @PostMapping("/updatePwdNoLogin")
    public NCResult updatePwdNoLogin(@RequestBody LoginVO loginVO) {
        loginVO.setPassword(commonService.rsaDecrypt(loginVO.getRsaTag(), loginVO.getPassword()));
        loginVO.setNewPassword(commonService.rsaDecrypt(loginVO.getRsaTag(), loginVO.getNewPassword()));
        adminService.updateAdminPassword(loginVO.getAccount(), loginVO.getPassword(), loginVO.getNewPassword(), null);
        return NCResult.ok();
    }

}
