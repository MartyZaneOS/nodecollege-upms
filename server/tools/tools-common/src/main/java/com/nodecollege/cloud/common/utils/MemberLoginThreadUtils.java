package com.nodecollege.cloud.common.utils;

import com.nodecollege.cloud.common.enums.ErrorEnum;
import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.MemberLoginInfo;
import lombok.Data;

/**
 * 成员线程共享数据
 *
 * @author LC
 * @date 18:33 2019/12/3
 **/
@Data
public class MemberLoginThreadUtils {

    private static final ThreadLocal<MemberLoginInfo> THREAD_LOCAL_MEMBER = new ThreadLocal<>();

    private MemberLoginThreadUtils() {
    }

    /**
     * 清除线程中的用户数据
     **/
    public static void reset() {
        THREAD_LOCAL_MEMBER.remove();
    }

    /**
     * 设置登陆用户信息
     **/
    public static void setLoginInfo(MemberLoginInfo loginMemberInfo) {
        THREAD_LOCAL_MEMBER.set(loginMemberInfo);
    }

    /**
     * 获取登陆用户信息
     **/
    public static MemberLoginInfo getLoginMemberThrow() {
        MemberLoginInfo loginMemberInfo = getLoginInfo();
        if (NCUtils.isNullOrEmpty(loginMemberInfo)) {
            throw new NCException(ErrorEnum.LOGIN_TIME_OUT);
        }
        return loginMemberInfo;
    }

    /**
     * 获取登陆用户信息
     **/
    public static MemberLoginInfo getLoginInfo() {
        return THREAD_LOCAL_MEMBER.get();
    }
}
