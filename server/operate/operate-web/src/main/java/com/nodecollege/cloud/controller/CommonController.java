package com.nodecollege.cloud.controller;

import com.nodecollege.cloud.common.constants.HeaderConstants;
import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.service.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * @author LC
 * @date 2020/11/16 14:03
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Autowired
    private CommonService commonService;

    /**
     * 获取公钥
     *
     * @param response
     * @return
     */
    @PostMapping("/getPublicKey")
    public NCResult<String> getKeyOfRSA(HttpServletResponse response) {
        Map<String, Object> map = commonService.getPublicKey();
        Cookie rsaTag = new Cookie(HeaderConstants.RSA_TAG, map.get("rsaTag").toString());
        rsaTag.setMaxAge(Integer.valueOf(map.get("expire").toString()));
        rsaTag.setPath("/");
        response.addCookie(rsaTag);
        return NCResult.ok(map.get("publicKeyBase64"));
    }

    /**
     * 生成图片验证码
     *
     * @param request
     * @param response
     */
    @GetMapping("/createImageCert")
    public void createImageCert(HttpServletRequest request, HttpServletResponse response) {
        String imageCertSessionId = NCUtils.getCookieValue(HeaderConstants.IMAGE_CERT_SESSION_ID, request);
        if (null == imageCertSessionId) {
            imageCertSessionId = NCUtils.getUUID();
        }
        BufferedImage bufferedImage = commonService.createImageCert(imageCertSessionId);
        Cookie rsaTag = new Cookie(HeaderConstants.IMAGE_CERT_SESSION_ID, imageCertSessionId);
        rsaTag.setMaxAge(5 * 60);
        rsaTag.setPath("/");
        response.addCookie(rsaTag);
        byte[] captcha = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ServletOutputStream sout = null;
        try {
            ImageIO.write(bufferedImage, "jpg", out);
            captcha = out.toByteArray();
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);
            response.setContentType("image/jpeg");
            sout = response.getOutputStream();
            sout.write(captcha);
        } catch (IOException e) {
            log.error("系统异常！", e);
            throw new NCException("", "系统异常！");
        } finally {
            try {
                if (null != sout) {
                    sout.flush();
                    sout.close();
                }
            } catch (IOException e) {
                log.error("系统异常！", e);
                throw new NCException("-1", "系统异常！");
            }
        }
    }
}
