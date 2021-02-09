package com.nodecollege.cloud.service;

import java.awt.image.BufferedImage;
import java.util.Map;

/**
 * @author LC
 * @date 2020/11/16 13:34
 */
public interface CommonService {

    /**
     * 获取公钥
     * @return
     */
    Map<String, Object> getPublicKey();

    /**
     * 根据rsa标志获取私钥
     */
    String getPrivateKey(String rsaTag);

    /**
     * rsa解密
     * @param rsaTag rsa标志
     * @param mw 密文
     * @return 明文
     */
    String rsaDecrypt(String rsaTag, String mw);

    // 创建验证码图片
    BufferedImage createImageCert(String imageCertSessionId);

    // 验证验证码
    Boolean checkImageCert(String imageCertSessionId, String imageCert);
}
