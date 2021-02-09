package com.nodecollege.cloud.service.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.nodecollege.cloud.common.constants.RedisConstants;
import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.common.utils.RedisUtils;
import com.nodecollege.cloud.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LC
 * @date 2020/11/16 13:34
 */
@Service
public class CommonServiceImpl implements CommonService {

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 1、验证码工具
     */
    @Autowired
    DefaultKaptcha defaultKaptcha;

    @Override
    public Map<String, Object> getPublicKey() {
        // 从redis中获取 rsa标识
        String rsaTag = redisUtils.get(RedisConstants.RSA_TAG, String.class);
        String publicKeyBase64 = null;
        Long expire = 60 * 60 * 24L;
        if (rsaTag == null) {
            // rsa标识为空，生成新的rsa，有效期25个小时，生成rsa标识，有效期24个小时
            // 差一个小时的原因是为了防止 刚获取到公钥，停了一会，然后公钥就失效了
            rsaTag = NCUtils.getUUID().substring(0, 8);
            RSA rsa = new RSA();
            publicKeyBase64 = rsa.getPublicKeyBase64();
            String privateKeyBase64 = rsa.getPrivateKeyBase64();
            redisUtils.set(RedisConstants.RSA_TAG, rsaTag, 60 * 60 * 24);
            redisUtils.set(RedisConstants.RSA_PUBLIC_KEY + rsaTag, publicKeyBase64, 60 * 60 * 25);
            redisUtils.set(RedisConstants.RSA_PRIVATE_KEY + rsaTag, privateKeyBase64, 60 * 60 * 25);
        } else {
            // 根据rsa标识从redis中获取公钥
            publicKeyBase64 = redisUtils.get(RedisConstants.RSA_PUBLIC_KEY + rsaTag, String.class);
            expire = redisUtils.getExpire(RedisConstants.RSA_TAG);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("rsaTag", rsaTag);
        map.put("publicKeyBase64", publicKeyBase64);
        map.put("expire", expire);
        return map;
    }

    @Override
    public String getPrivateKey(String rsaTag) {
        String privateKeyBase64 = redisUtils.get(RedisConstants.RSA_PRIVATE_KEY + rsaTag, String.class);
        if (privateKeyBase64 == null) {
            throw new NCException("-1", "RSA已失效，请重新生成获取！");
        }
        return privateKeyBase64;
    }

    @Override
    public String rsaDecrypt(String rsaTag, String mw) {
        NCUtils.nullOrEmptyThrow(rsaTag);
        NCUtils.nullOrEmptyThrow(mw);

        String privateKeyBase64 = getPrivateKey(rsaTag);
        RSA rsa = new RSA(privateKeyBase64, null);
        // 密码的密文先进行base64解码，之后再进行解密
        byte[] decrypt = rsa.decrypt(Base64.decode(mw), KeyType.PrivateKey);
        return StrUtil.str(decrypt, CharsetUtil.CHARSET_UTF_8);
    }

    @Override
    public BufferedImage createImageCert(String imageCertSessionId) {
        // 生产验证码字符串并保存到session中
        String imageCert = defaultKaptcha.createText();
        // 使用生产的验证码字符串返回一个BufferedImage对象并转为byte写入到byte数组中
        BufferedImage challenge = defaultKaptcha.createImage(imageCert);
        // 验证码有效期5分钟
        redisUtils.set(RedisConstants.IMAGE_CERT + imageCertSessionId, imageCert, 5 * 60);
        return challenge;
    }

    @Override
    public Boolean checkImageCert(String imageCertSessionId, String imageCert) {
        if (imageCertSessionId == null || imageCert == null) return false;
        String exImageCert = redisUtils.get(RedisConstants.IMAGE_CERT + imageCertSessionId, String.class);
        if (null == exImageCert || !exImageCert.equalsIgnoreCase(imageCert)) {
            return false;
        }
        redisUtils.delete(RedisConstants.IMAGE_CERT + imageCertSessionId);
        return true;
    }
}
