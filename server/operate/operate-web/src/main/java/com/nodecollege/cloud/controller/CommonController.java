package com.nodecollege.cloud.controller;

import com.nodecollege.cloud.common.constants.HeaderConstants;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author LC
 * @date 2020/11/16 14:03
 */
@RestController
@RequestMapping("/common")
public class CommonController {

    @Autowired
    private CommonService commonService;

    @PostMapping("/getPublicKey")
    public NCResult<String> getKeyOfRSA(HttpServletResponse response) {
        Map<String, Object> map = commonService.getPublicKey();
        Cookie rsaTag = new Cookie(HeaderConstants.RSA_TAG, map.get("rsaTag").toString());
        rsaTag.setMaxAge(Integer.valueOf(map.get("expire").toString()));
        rsaTag.setPath("/");
        response.addCookie(rsaTag);
        return NCResult.ok(map.get("publicKeyBase64"));
    }

}
