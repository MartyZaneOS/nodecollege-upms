package com.nodecollege.cloud.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 密码工具类
 * @author LC
 * @date 2019/8/20 13:55
 */
public class PasswordUtil {

    private static final Logger logger = LoggerFactory.getLogger(PasswordUtil.class);

    /**
     * 加盐参数
     */
    public final static String HASH_ALGORITHM_NAME = "MD5";

    /**
     * 循环次数
     */
    public final static int HASH_ITERATIONS = 1024;


    private static final char[] DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 获取随机盐值
     * @return
     */
    public static String getRandomSalt() {
        String model = "abcdefghijklmnopqrstuvwxyz1234567890";
        StringBuffer salt = new StringBuffer();
        char[] m = model.toCharArray();
        for (int i = 0; i < 6; i++) {
            char c = m[(int) (Math.random() * 36)];
            // 保证六位随机数之间没有重复的
            if (salt.indexOf(String.valueOf(c)) == 0) {
                i--;
                continue;
            }
            salt.append(c);
        }
        return salt.toString();
    }

    /**
     * shiro密码加密工具类
     * @param password   密码
     * @param saltSource 密码盐
     * @return
     */
    public static String md5(String password, String saltSource) {
        return md5(HASH_ALGORITHM_NAME, password, saltSource, HASH_ITERATIONS);
    }

    public static String md5(String HASH_ALGORITHM_NAME, String password, String saltSource, int HASH_ITERATIONS) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM_NAME);
            byte[] salt = saltSource.getBytes("UTF-8");
            byte[] bytes = password.getBytes("UTF-8");
            if (salt != null) {
                digest.reset();
                digest.update(salt);
            }
            byte[] hashed = digest.digest(bytes);
            int iterations = HASH_ITERATIONS - 1;

            for (int i = 0; i < iterations; ++i) {
                digest.reset();
                hashed = digest.digest(hashed);
            }
            char[] encodedChars = encode(hashed);
            return new String(encodedChars);
        } catch (NoSuchAlgorithmException e) {
            logger.error("无该类型加密方式" + e.getMessage(), e);
            throw new RuntimeException("无该类型加密方式" + HASH_ALGORITHM_NAME);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException("无该编码类型" + e.getMessage());
        }
    }

    public static char[] encode(byte[] data) {

        int l = data.length;
        char[] out = new char[l << 1];
        int i = 0;

        for (int var4 = 0; i < l; ++i) {
            out[var4++] = DIGITS[(240 & data[i]) >>> 4];
            out[var4++] = DIGITS[15 & data[i]];
        }

        return out;
    }
}
