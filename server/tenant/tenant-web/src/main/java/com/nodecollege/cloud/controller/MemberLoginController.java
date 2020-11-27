package com.nodecollege.cloud.controller;

import com.nodecollege.cloud.client.utils.NCLoginUtils;
import com.nodecollege.cloud.common.annotation.ApiAnnotation;
import com.nodecollege.cloud.common.constants.HeaderConstants;
import com.nodecollege.cloud.common.enums.ErrorEnum;
import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.NCLoginUserVO;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.po.OperateUserTenant;
import com.nodecollege.cloud.common.model.po.TenantMember;
import com.nodecollege.cloud.common.model.vo.LoginVO;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.service.CommonService;
import com.nodecollege.cloud.service.LoginService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @author LC
 * @date 2020/10/21 19:59
 */
@RestController
@RequestMapping("/member")
public class MemberLoginController {


    @Autowired
    private NCLoginUtils loginUtils;

    @Autowired
    private LoginService loginService;

    @Autowired
    private CommonService commonService;

    /**
     * 用户登陆
     */
    @ApiAnnotation(modularName = "成员登陆", description = "根据用户租户信息登录")
    @PostMapping("/login")
    public NCResult<NCLoginUserVO> login(@RequestBody LoginVO loginVO, HttpServletRequest request, HttpServletResponse response) {
        loginVO.setPassword(commonService.rsaDecrypt(loginVO.getRsaTag(), loginVO.getPassword()));
        String loginTenantCode = request.getHeader(HeaderConstants.LOGIN_TENANT_CODE);
        if (StringUtils.isBlank(loginTenantCode)) {
            throw new NCException("", "登录租户代码为空！");
        }

        // 登陆验证
        NCLoginUserVO token = loginService.login(loginVO, loginTenantCode);
        Cookie accessTokenCookie = new Cookie(HeaderConstants.MEMBER_ACCESS_TOKEN, token.getAccessToken());
        accessTokenCookie.setMaxAge(token.getExpire().intValue());
        accessTokenCookie.setPath("/");
        Cookie uuidCookie = new Cookie(HeaderConstants.MEMBER_UUID, token.getUuid());
        uuidCookie.setMaxAge(token.getExpire().intValue());
        uuidCookie.setPath("/");
        response.addCookie(accessTokenCookie);
        response.addCookie(uuidCookie);
        return NCResult.ok(Arrays.asList(token));
    }

    /**
     * 用户登陆
     */
    @ApiAnnotation(modularName = "成员登陆", description = "根据用户租户信息登录")
    @PostMapping("/loginByUserTenant")
    public NCResult<NCLoginUserVO> loginByUserTenant(@RequestBody OperateUserTenant userTenant, HttpServletResponse response) {
        NCLoginUserVO loginUser = loginUtils.getUserLoginInfo();
        NCUtils.nullOrEmptyThrow(loginUser, ErrorEnum.LOGIN_TIME_OUT);
        TenantMember member = new TenantMember();
        member.setUserId(loginUser.getLoginId());
        member.setTenantId(userTenant.getTenantId());

        // 登陆验证
        NCLoginUserVO token = loginService.loginByUserTenant(member);
        Cookie accessTokenCookie = new Cookie(HeaderConstants.MEMBER_ACCESS_TOKEN, token.getAccessToken());
        accessTokenCookie.setMaxAge(token.getExpire().intValue());
        accessTokenCookie.setPath("/");
        Cookie uuidCookie = new Cookie(HeaderConstants.MEMBER_UUID, token.getUuid());
        uuidCookie.setMaxAge(token.getExpire().intValue());
        uuidCookie.setPath("/");
        response.addCookie(accessTokenCookie);
        response.addCookie(uuidCookie);
        return NCResult.ok(Arrays.asList(token));
    }

    /**
     * 用户注销请求
     */
    @ApiAnnotation(modularName = "成员登陆", description = "成员注销登录")
    @RequestMapping("/logout")
    public NCResult logout(HttpServletResponse response) {
        NCLoginUserVO loginMember = loginUtils.getMemberLoginInfo();
        if (loginMember == null) {
            return NCResult.ok();
        }
        loginService.logout(loginMember.getAccessToken());
        Cookie accessToken = new Cookie(HeaderConstants.MEMBER_ACCESS_TOKEN, null);
        accessToken.setMaxAge(0);
        accessToken.setPath("/");
        Cookie uuid = new Cookie(HeaderConstants.MEMBER_UUID, null);
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
    public NCResult updatePwdNoLogin(@RequestBody LoginVO loginVO, HttpServletRequest request) {
        loginVO.setPassword(commonService.rsaDecrypt(loginVO.getRsaTag(), loginVO.getPassword()));
        loginVO.setNewPassword(commonService.rsaDecrypt(loginVO.getRsaTag(), loginVO.getNewPassword()));
        String tenantCode = request.getHeader(HeaderConstants.LOGIN_TENANT_CODE);
        loginService.updatePwdNoLogin(loginVO, tenantCode);
        return NCResult.ok();
    }
}
