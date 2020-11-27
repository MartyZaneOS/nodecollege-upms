package com.nodecollege.cloud.common.utils;

import com.nodecollege.cloud.common.enums.ErrorEnum;
import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.UserLoginInfo;
import lombok.Data;

/**
 * 线程共享数据
 *
 * @author LC
 * @date 18:33 2019/12/3
 **/
@Data
public class UserLoginThreadUtils {

    private static final ThreadLocal<UserLoginInfo> THREAD_LOCAL_UER = new ThreadLocal<>();

    private UserLoginThreadUtils() {
    }

    /**
     * 清除线程中的用户数据
     **/
    public static void reset() {
        THREAD_LOCAL_UER.remove();
    }

    /**
     * 设置登陆用户信息
     **/
    public static void setLoginInfo(UserLoginInfo loginMemberInfo) {
        THREAD_LOCAL_UER.set(loginMemberInfo);
    }

    /**
     * 获取登陆用户信息
     **/
    public static UserLoginInfo getLoginUserThrow() {
        UserLoginInfo loginMemberInfo = getLoginInfo();
        if (NCUtils.isNullOrEmpty(loginMemberInfo)) {
            throw new NCException(ErrorEnum.LOGIN_TIME_OUT);
        }
        return loginMemberInfo;
    }

    /**
     * 获取登陆用户信息
     **/
    public static UserLoginInfo getLoginInfo() {
        return THREAD_LOCAL_UER.get();
    }
}
