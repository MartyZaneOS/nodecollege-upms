package com.nodecollege.cloud.client.utils;

import com.nodecollege.cloud.common.constants.HeaderConstants;
import com.nodecollege.cloud.common.constants.RedisConstants;
import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.MenuVO;
import com.nodecollege.cloud.common.model.NCLoginUserVO;
import com.nodecollege.cloud.common.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 登录工具类
 *
 * @author LC
 * @date 2020/9/23 20:45
 */
@Slf4j
@Component
public class NCLoginUtils {

    @Value("${spring.application.name:default}")
    private String applicationName;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private RedisUtils redisUtils;

    private NCLoginUtils() {
    }

    /**
     * 获取管理员登录信息
     */
    public NCLoginUserVO getAdminLoginInfo() {
        if (request == null) return null;
        try {
            // 访问token
            String accessToken = getHeaderParam(HeaderConstants.ADMIN_ACCESS_TOKEN, request);
            // 随机码
            String uuid = getHeaderParam(HeaderConstants.ADMIN_UUID, request);
            String tokenKey = RedisConstants.ADMIN_LOGIN_INFO + accessToken;
            NCLoginUserVO loginInfo = redisUtils.get(tokenKey, NCLoginUserVO.class);
            if (loginInfo == null || !loginInfo.getUuid().equals(uuid)) return null;
            return loginInfo;
        } catch (IllegalStateException e) {
            log.error("初始化错误！", e);
        }
        return null;
    }

    /**
     * 获取授权管理员机构代码列表
     */
    public List<String> getPowerAdminOrgCodeList() {
        NCLoginUserVO admin = getAdminLoginInfo();
        if (admin == null) {
            throw new NCException("", "管理员未登陆！");
        }
        String origin = request.getHeader("Origin");
        String referer = request.getHeader("Referer");
        // 页面路径
        String pagePath = referer.substring(origin.length());
        String apiUrl = request.getRequestURI();
        Set<String> orgCodeSet = getOrgCodeByPagePath(admin.getMenuTree(), pagePath, applicationName, apiUrl);
        if (orgCodeSet == null) {
            return null;
        }
        return new ArrayList<>(orgCodeSet);
    }

    /**
     * 获取用户登录信息
     */
    public NCLoginUserVO getUserLoginInfo() {
        if (request == null) return null;
        // 访问token
        String accessToken = getHeaderParam(HeaderConstants.USER_ACCESS_TOKEN, request);
        // 随机码
        String uuid = getHeaderParam(HeaderConstants.USER_UUID, request);
        String tokenKey = RedisConstants.USER_LOGIN_INFO + accessToken;
        NCLoginUserVO loginInfo = redisUtils.get(tokenKey, NCLoginUserVO.class);
        if (loginInfo == null || !loginInfo.getUuid().equals(uuid)) return null;
        return loginInfo;
    }

    /**
     * 获取授权用户机构代码列表
     */
    public List<String> getPowerUserOrgCodeList() {
        NCLoginUserVO user = getUserLoginInfo();
        if (user == null) {
            throw new NCException("", "用户未登陆！");
        }
        String origin = request.getHeader("Origin");
        String referer = request.getHeader("Referer");
        // 页面路径
        String pagePath = referer.substring(origin.length());
        String apiUrl = request.getRequestURI();
        Set<String> orgCodeSet = getOrgCodeByPagePath(user.getMenuTree(), pagePath, applicationName, apiUrl);
        if (orgCodeSet == null) {
            return null;
        }
        return new ArrayList<>(orgCodeSet);
    }

    /**
     * 获取成员登录信息
     */
    public NCLoginUserVO getMemberLoginInfo() {
        if (request == null) return null;
        // 访问token
        String accessToken = getHeaderParam(HeaderConstants.MEMBER_ACCESS_TOKEN, request);
        // 随机码
        String uuid = getHeaderParam(HeaderConstants.MEMBER_UUID, request);
        String tokenKey = RedisConstants.MEMBER_LOGIN_INFO + accessToken;
        NCLoginUserVO loginInfo = redisUtils.get(tokenKey, NCLoginUserVO.class);
        if (loginInfo == null || !loginInfo.getUuid().equals(uuid)) return null;
        return loginInfo;
    }

    /**
     * 获取授权租户成员机构代码列表
     * 返回null 只能操作用户自己的数据
     */
    public List<String> getPowerMemberOrgCodeList() {
        NCLoginUserVO member = getMemberLoginInfo();
        if (member == null) {
            throw new NCException("", "租户成员未登陆！");
        }
        String origin = request.getHeader("Origin");
        String referer = request.getHeader("Referer");
        // 页面路径
        String pagePath = referer.substring(origin.length());
        String apiUrl = request.getRequestURI();
        Set<String> orgCodeSet = getOrgCodeByPagePath(member.getMenuTree(), pagePath, applicationName, apiUrl);
        if (orgCodeSet == null) {
            return null;
        }
        return new ArrayList<>(orgCodeSet);
    }

    /**
     * 根据页面路径，接口地址获取授权机构代码
     */
    private Set<String> getOrgCodeByPagePath(List<MenuVO> menuTree, String pagePath, String appName, String apiUrl) {
        if (menuTree == null) return null;
        for (MenuVO menu : menuTree) {
            if (menu.getMenuType() == 1 && menu.getPagePath().equals(pagePath))
                return getOrgCodeByApiUrl(menu.getChildren(), appName, apiUrl);
            Set<String> list = getOrgCodeByPagePath(menu.getChildren(), pagePath, appName, apiUrl);
            if (list != null) return list;
        }
        return null;
    }

    /**
     * 根据接口地址获取授权机构代码
     */
    private Set<String> getOrgCodeByApiUrl(List<MenuVO> menuTree, String appName, String apiUrl) {
        if (menuTree == null) return null;
        for (MenuVO button : menuTree) {
            if (button.getAppName().equals(appName) && button.getApiUrl().equals(apiUrl))
                return button.getOrgCodeList();
            Set<String> list = getOrgCodeByApiUrl(button.getChildren(), appName, apiUrl);
            if (list != null) return list;
        }
        return null;
    }

    private static String getHeaderParam(String key, HttpServletRequest request) {
        String paramValue = request.getHeader(key);
        if (StringUtils.isBlank(paramValue)) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (int i = 0; i < cookies.length; ++i) {
                    Cookie cookie = cookies[i];
                    String cookieName = cookie.getName();
                    if (key.equals(cookieName)) {
                        paramValue = cookie.getValue();
                        break;
                    }
                }
            }
        }
        return paramValue;
    }
}
