package com.nodecollege.cloud.common.utils;

import com.nodecollege.cloud.common.exception.NCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;

public class Base64Util {
    private static Logger logger = LoggerFactory.getLogger(Base64Util.class);

    public static MultipartFile base64ToMultipart(String base64) {
        try {
            String[] baseStr = base64.split(",");

            BASE64Decoder decoder = new BASE64Decoder();
            byte[] b = new byte[0];
            b = decoder.decodeBuffer(baseStr[1]);

            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }

            return new BASE64DecodedMultipartFile(b, baseStr[0]);
        } catch (IOException e) {
            logger.error("base64转Multipart失败", e);
            throw new NCException("", "base64转Multipart失败！");
        }
    }

    public static String multipartToBase64(MultipartFile multipartFile) {
        String base64EncoderImg = "";
        try {
            BASE64Encoder base64Encoder = new BASE64Encoder();
            base64EncoderImg = "data:" + multipartFile.getContentType() + ";base64," + base64Encoder.encode(multipartFile.getBytes());
        } catch (IOException e) {
            logger.error("Multipart转base64失败", e);
            throw new NCException("", "Multipart转base64失败！");
        }
        return base64EncoderImg;
    }
}