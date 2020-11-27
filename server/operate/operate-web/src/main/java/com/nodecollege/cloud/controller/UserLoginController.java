package com.nodecollege.cloud.controller;

import com.nodecollege.cloud.client.utils.NCLoginUtils;
import com.nodecollege.cloud.common.annotation.ApiAnnotation;
import com.nodecollege.cloud.common.constants.HeaderConstants;
import com.nodecollege.cloud.common.model.NCLoginUserVO;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.vo.LoginVO;
import com.nodecollege.cloud.service.CommonService;
import com.nodecollege.cloud.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * 登陆controller
 *
 * @author LC
 * @date 2019/6/17 10:07
 */
@RestController
@RequestMapping("/user")
public class UserLoginController {

    private static final Logger logger = LoggerFactory.getLogger(UserLoginController.class);

    @Autowired
    private LoginService loginService;

    @Autowired
    private NCLoginUtils loginUtils;

    @Autowired
    private CommonService commonService;

    /**
     * 用户注册
     */
    @ApiAnnotation(modularName = "用户登陆", description = "用户注册")
    @PostMapping("/register")
    public NCResult logon(@RequestBody LoginVO loginVO) {
        loginService.register(loginVO);
        return NCResult.ok();
    }

    /**
     * 用户登陆
     */
    @ApiAnnotation(modularName = "用户登陆", description = "用户登陆")
    @PostMapping("/login")
    public NCResult<NCLoginUserVO> login(@RequestBody LoginVO loginVO, HttpServletResponse response) {
        // 登陆验证
        loginVO.setPassword(commonService.rsaDecrypt(loginVO.getRsaTag(), loginVO.getPassword()));
        NCLoginUserVO token = loginService.login(loginVO);
        Cookie accessTokenCookie = new Cookie(HeaderConstants.USER_ACCESS_TOKEN, token.getAccessToken());
        accessTokenCookie.setMaxAge(token.getExpire().intValue());
        accessTokenCookie.setPath("/");
        Cookie uuidCookie = new Cookie(HeaderConstants.USER_UUID, token.getUuid());
        uuidCookie.setMaxAge(token.getExpire().intValue());
        uuidCookie.setPath("/");
        response.addCookie(accessTokenCookie);
        response.addCookie(uuidCookie);
        return NCResult.ok(token);
    }

    /**
     * 用户登陆
     */
    @ApiAnnotation(modularName = "用户登陆", description = "根据token获取用户登录信息！")
    @PostMapping("/getLoginUserInfoByToken")
    public NCResult<NCLoginUserVO> getLoginUserInfoByToken(@RequestBody LoginVO loginVO) {
        return NCResult.ok(loginService.getLoginUserInfo(loginVO));
    }

    /**
     * 用户注销请求
     */
    @ApiAnnotation(modularName = "用户登陆", description = "用户注销请求")
    @RequestMapping("/logout")
    public NCResult logout(HttpServletResponse response) {
        NCLoginUserVO loginUser = loginUtils.getUserLoginInfo();
        if (loginUser == null) {
            return NCResult.ok();
        }
        loginService.logout(loginUser.getAccessToken());
        Cookie accessToken = new Cookie(HeaderConstants.USER_ACCESS_TOKEN, null);
        accessToken.setMaxAge(0);
        accessToken.setPath("/");
        Cookie uuid = new Cookie(HeaderConstants.USER_UUID, null);
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
        loginService.updatePwdNoLogin(loginVO);
        return NCResult.ok();
    }

}
