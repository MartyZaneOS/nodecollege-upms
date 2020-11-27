package com.nodecollege.cloud.utils;

import com.nodecollege.cloud.common.constants.RedisConstants;
import com.nodecollege.cloud.common.model.NCLoginUserVO;
import com.nodecollege.cloud.common.model.NCRequest;
import com.nodecollege.cloud.common.utils.RedisUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 登录工具类
 *
 * @author LC
 * @date 2020/9/23 20:45
 */
@Component
public class ChatLoginUtils {

    private static final ThreadLocal<Map<String, NCLoginUserVO>> THREAD_LOCAL_UER = new ThreadLocal<>();

    @Value("${spring.application.name:default}")
    private String applicationName;

    private static final String ADMIN = "ADMIN";
    private static final String USER = "USER";
    private static final String MEMBER = "MEMBER";

    @Autowired
    private RedisUtils redisUtils;

    private ChatLoginUtils() {
    }

    /**
     * 清除线程中的用户数据
     **/
    public static void reset() {
        THREAD_LOCAL_UER.remove();
    }

    public void handlerLogin(NCRequest request) {
        THREAD_LOCAL_UER.remove();
        Map<String, NCLoginUserVO> map = THREAD_LOCAL_UER.get();
        if (map == null) {
            map = new HashMap<>();
        }
        // 用户登录信息
        String accessToken = request.getAccessToken();
        String uuid = request.getUuid();
        if (StringUtils.isNotBlank(accessToken) && StringUtils.isNotBlank(uuid)) {
            String tokenKey = RedisConstants.USER_LOGIN_INFO + accessToken;
            NCLoginUserVO loginInfo = redisUtils.get(tokenKey, NCLoginUserVO.class);
            if (loginInfo != null && loginInfo.getUuid().equals(uuid)) {
                map.put(USER, loginInfo);
                THREAD_LOCAL_UER.set(map);
            }
        }
        // 管理员登录信息
        String adminAccessToken = request.getAdminAccessToken();
        String adminUuid = request.getAdminUuid();
        if (StringUtils.isNotBlank(adminAccessToken) && StringUtils.isNotBlank(adminUuid)) {
            String tokenKey = RedisConstants.ADMIN_LOGIN_INFO + adminAccessToken;
            NCLoginUserVO loginInfo = redisUtils.get(tokenKey, NCLoginUserVO.class);
            if (loginInfo != null && loginInfo.getUuid().equals(adminUuid)) {
                map.put(ADMIN, loginInfo);
                THREAD_LOCAL_UER.set(map);
            }
        }
        // 管理员登录信息
        String memberAccessToken = request.getMemberAccessToken();
        String memberUuid = request.getMemberUuid();
        if (StringUtils.isNotBlank(memberAccessToken) && StringUtils.isNotBlank(memberUuid)) {
            String tokenKey = RedisConstants.MEMBER_LOGIN_INFO + memberAccessToken;
            NCLoginUserVO loginInfo = redisUtils.get(tokenKey, NCLoginUserVO.class);
            if (loginInfo != null && loginInfo.getUuid().equals(adminUuid)) {
                map.put(MEMBER, loginInfo);
                THREAD_LOCAL_UER.set(map);
            }
        }
    }

    /**
     * 获取管理员登录信息
     */
    public NCLoginUserVO getAdminLoginInfo() {
        Map<String, NCLoginUserVO> map = THREAD_LOCAL_UER.get();
        if (map != null && map.get(ADMIN) != null) {
            return map.get(ADMIN);
        }
        return null;
    }

    /**
     * 获取用户登录信息
     */
    public NCLoginUserVO getUserLoginInfo() {
        Map<String, NCLoginUserVO> map = THREAD_LOCAL_UER.get();
        if (map != null && map.get(USER) != null) {
            return map.get(USER);
        }
        return null;
    }

    /**
     * 获取成员登录信息
     */
    public NCLoginUserVO getMemberLoginInfo() {
        Map<String, NCLoginUserVO> map = THREAD_LOCAL_UER.get();
        if (map != null && map.get(MEMBER) != null) {
            return map.get(MEMBER);
        }
        return null;
    }
}
