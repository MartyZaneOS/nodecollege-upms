package com.nodecollege.cloud.service.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.nodecollege.cloud.common.constants.RedisConstants;
import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.common.utils.RedisUtils;
import com.nodecollege.cloud.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author LC
 * @date 2020/11/16 13:34
 */
@Service
public class CommonServiceImpl implements CommonService {

    @Autowired
    private RedisUtils redisUtils;

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
}
