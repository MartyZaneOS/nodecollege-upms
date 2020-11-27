package com.nodecollege.cloud.service;

import java.util.Map;

/**
 * @author LC
 * @date 2020/11/16 13:34
 */
public interface CommonService {

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
}
