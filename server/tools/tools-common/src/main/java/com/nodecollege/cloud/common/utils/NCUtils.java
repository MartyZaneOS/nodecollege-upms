package com.nodecollege.cloud.common.utils;

import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.exception.BaseException;
import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.CodeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

/**
 * NC工具类
 */
public class NCUtils {

    private static final Logger logger = LoggerFactory.getLogger(NCUtils.class);

    public NCUtils() {
    }

    /**
     * 判断不为空
     */
    public static boolean isNotNullOrNotEmpty(Object obj) {
        return !isNullOrEmpty(obj);
    }

    /**
     * 判断为空
     */
    public static boolean isNullOrEmpty(Object obj) {
        if (obj == null) {
            return true;
        } else if (obj instanceof CharSequence) {
            return ((CharSequence) obj).length() == 0;
        } else if (obj instanceof Collection) {
            return ((Collection) obj).isEmpty();
        } else if (obj instanceof Map) {
            return ((Map) obj).isEmpty();
        } else if (!(obj instanceof Object[])) {
            return false;
        } else {
            Object[] object = (Object[]) ((Object[]) obj);
            if (object.length == 0) {
                return true;
            } else {
                boolean empty = true;

                for (int i = 0; i < object.length; ++i) {
                    if (!isNullOrEmpty(object[i])) {
                        empty = false;
                        break;
                    }
                }

                return empty;
            }
        }
    }

    /**
     * 获取请求id
     */
    public static String getRequestId() {
        return getUUID().substring(0, 16);
    }

    /**
     * 获取uuid
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 为空抛出错误
     *
     * @param object
     * @param e
     */
    public static void nullOrEmptyThrow(Object object, RuntimeException e) {
        if (isNullOrEmpty(object)) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    public static void nullOrEmptyThrow(Object object, CodeMessage codeMessage) {
        if (isNullOrEmpty(object)) {
            logger.error(codeMessage.getMessage());
            codeMessage.throwMsg();
        }
    }

    public static void nullOrEmptyThrow(Object object, String code, String message) {
        if (isNullOrEmpty(object)) {
            logger.error(message);
            throw new NCException(code, message);
        }
    }

    public static void nullOrEmptyThrow(Object object) {
        nullOrEmptyThrow(object, "-1", "参数缺失！");
    }
    /**
     * 不为空 抛出错误
     *
     * @param object
     * @param e
     */
    public static void notNullOrNotEmptyThrow(Object object, RuntimeException e) {
        if (isNotNullOrNotEmpty(object)) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    public static void notNullOrNotEmptyThrow(Object object, CodeMessage codeMessage) {
        if (isNotNullOrNotEmpty(object)) {
            logger.error(codeMessage.getMessage());
            codeMessage.throwMsg();
        }
    }

    public static void notNullOrNotEmptyThrow(Object object, String code, String message) {
        if (isNotNullOrNotEmpty(object)) {
            logger.error(message);
            throw new NCException(code, message);
        }
    }
    /**
     * 判断是否相等
     * 适用于基本类型和字符串
     *
     * @param o1
     * @param o2
     * @return
     */
    public static boolean equals(Object o1, Object o2) {
        if (o1 == null && o2 == null)
            return true;
        if (o1 == null || o2 == null)
            return false;
        if (isBaseType(o1) && isBaseType(o2))
            return o1.equals(o2);
        if (o1 instanceof String && o2 instanceof String)
            return o1.equals(o2);
        throw new NCException("-1", "只能比较基本类型或者字符串类型");
    }

    /**
     * 不相等 抛出错误
     */
    public static void unequalThrow(Object o1, Object o2, RuntimeException e) {
        if (!equals(o1, o2)) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 相等 抛出错误
     */
    public static void equalThrow(Object o1, Object o2, RuntimeException e) {
        if (equals(o1, o2)) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    public static boolean isBaseType(Object object) {
        Class className = object.getClass();
        if (className.equals(java.lang.Integer.class) ||
                className.equals(java.lang.Byte.class) ||
                className.equals(java.lang.Long.class) ||
                className.equals(java.lang.Double.class) ||
                className.equals(java.lang.Float.class) ||
                className.equals(java.lang.Character.class) ||
                className.equals(java.lang.Short.class) ||
                className.equals(java.lang.Boolean.class)) {
            return true;
        }
        return false;
    }


    /**
     * 获取访客ip
     *
     * @param request
     * @return
     */
    public static String getRequestIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && ip.length() != 0 && !NCConstants.UNKNOWN.equalsIgnoreCase(ip)) {
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if (index != -1) {
                ip = ip.substring(0, index);
            }
        }
        if (ip == null || ip.length() == 0 || NCConstants.UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || NCConstants.UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || NCConstants.UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || NCConstants.UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || NCConstants.UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || NCConstants.UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
