package com.nodecollege.cloud.controller;

import com.nodecollege.cloud.client.utils.NCLoginUtils;
import com.nodecollege.cloud.common.annotation.ApiAnnotation;
import com.nodecollege.cloud.common.constants.HeaderConstants;
import com.nodecollege.cloud.common.enums.ErrorEnum;
import com.nodecollege.cloud.common.model.NCLoginUserVO;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.po.TenantMember;
import com.nodecollege.cloud.common.model.vo.LoginVO;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.service.CommonService;
import com.nodecollege.cloud.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * @author LC
 * @date 2020/11/23 16:53
 */
@RestController
@RequestMapping("/memberCenter")
public class MemberCenterController {

    @Autowired
    private NCLoginUtils loginUtils;

    @Autowired
    private MemberService memberService;

    @Autowired
    private CommonService commonService;


    @ApiAnnotation(modularName = "成员中心", description = "变更默认机构角色选项")
    @PostMapping("/changeDefaultOption")
    public NCResult<NCLoginUserVO> changeDefaultOption(@RequestBody TenantMember member, HttpServletResponse response) {
        // 获取当前登录人信息
        NCLoginUserVO loginMember = loginUtils.getMemberLoginInfo();
        NCUtils.nullOrEmptyThrow(loginMember, ErrorEnum.LOGIN_TIME_OUT);
        member.setMemberId(loginMember.getLoginId());
        loginMember = memberService.changeDefaultOption(loginMember, member);
        Cookie accessToken = new Cookie(HeaderConstants.MEMBER_ACCESS_TOKEN, loginMember.getAccessToken());
        accessToken.setMaxAge(loginMember.getExpire());
        accessToken.setPath("/");
        Cookie uuid = new Cookie(HeaderConstants.MEMBER_UUID, loginMember.getUuid());
        uuid.setMaxAge(loginMember.getExpire());
        uuid.setPath("/");
        response.addCookie(accessToken);
        response.addCookie(uuid);
        return NCResult.ok(loginMember);
    }

    @ApiAnnotation(modularName = "成员中心", description = "修改密码")
    @PostMapping("/updatePwd")
    public NCResult updatePwd(@RequestBody LoginVO loginVO) {
        loginVO.setPassword(commonService.rsaDecrypt(loginVO.getRsaTag(), loginVO.getPassword()));
        loginVO.setNewPassword(commonService.rsaDecrypt(loginVO.getRsaTag(), loginVO.getNewPassword()));

        NCLoginUserVO loginMember = loginUtils.getMemberLoginInfo();
        NCUtils.nullOrEmptyThrow(loginMember, ErrorEnum.LOGIN_TIME_OUT);
        memberService.updatePwd(loginMember.getLoginId(), loginVO.getPassword(), loginVO.getNewPassword());
        return NCResult.ok();
    }
}
