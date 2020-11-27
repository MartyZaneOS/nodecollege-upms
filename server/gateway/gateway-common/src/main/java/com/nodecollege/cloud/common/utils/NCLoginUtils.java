package com.nodecollege.cloud.common.utils;

import com.nodecollege.cloud.common.model.NCLoginUserVO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 登录工具类
 *
 * @author LC
 * @date 2020/9/23 20:45
 */
@Data
@Slf4j
public class NCLoginUtils {

    private static final ThreadLocal<Map<String, NCLoginUserVO>> THREAD_LOCAL_UER = new ThreadLocal<>();

    private static final String ADMIN = "ADMIN";
    private static final String USER = "USER";
    private static final String MEMBER = "MEMBER";

    private NCLoginUtils() {}

    /**
     * 清除线程中的用户数据
     **/
    public static void reset() {
        THREAD_LOCAL_UER.remove();
    }

    /**
     * 设置管理员登录信息
     */
    public static void setAdminLoginInfo(NCLoginUserVO adminInfo) {
        Map<String, NCLoginUserVO> map = THREAD_LOCAL_UER.get();
        if (map == null) {
            map = new HashMap<>();
        }
        map.put(ADMIN, adminInfo);
        THREAD_LOCAL_UER.set(map);
    }

    /**
     * 获取管理员登录信息
     */
    public static NCLoginUserVO getAdminLoginInfo() {
        if (THREAD_LOCAL_UER.get() != null) {
            return THREAD_LOCAL_UER.get().get(ADMIN);
        }
        return null;
    }

    /**
     * 设置用户登录信息
     */
    public static void setUserLoginInfo(NCLoginUserVO userInfo) {
        Map<String, NCLoginUserVO> map = THREAD_LOCAL_UER.get();
        if (map == null) {
            map = new HashMap<>();
        }
        map.put(USER, userInfo);
        THREAD_LOCAL_UER.set(map);
    }

    /**
     * 获取用户登录信息
     */
    public static NCLoginUserVO getUserLoginInfo() {
        if (THREAD_LOCAL_UER.get() != null) {
            return THREAD_LOCAL_UER.get().get(USER);
        }
        return null;
    }

    /**
     * 设置成员登录信息
     */
    public static void setMemerLoginInfo(NCLoginUserVO memberInfo) {
        Map<String, NCLoginUserVO> map = THREAD_LOCAL_UER.get();
        if (map == null) {
            map = new HashMap<>();
        }
        map.put(MEMBER, memberInfo);
        THREAD_LOCAL_UER.set(map);
    }

    /**
     * 获取成员登录信息
     */
    public static NCLoginUserVO getMemberLoginInfo() {
        if (THREAD_LOCAL_UER.get() != null) {
            return THREAD_LOCAL_UER.get().get(MEMBER);
        }
        return null;
    }
}
