package com.nodecollege.cloud.cache;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LC
 * @date 2020/10/13 20:19
 */
@Data
public class ConfigCache {
    // 默认密码
    public static String DEFAULT_PWD = "a123456";
    // 密码包含大写英文位数
    public static Integer CONTAIN_CAPITAL_ENGLISH = 0;
    // 密码包含小写英文位数
    public static Integer CONTAIN_LOWERCASE_ENGLISH = 0;
    // 密码包含数字位数
    public static Integer CONTAIN_NUMBER = 0;
    // 密码包含特殊字符位数
    public static Integer CONTAIN_SPECIAL = 0;
    // 密码和账号不能相同
    public static Boolean PWD_EQUAL_USER_CHECK = true;
    // 密码有效期
    public static Integer PASSWORD_VALIDITY = 0;
    // 密码长度
    public static Integer PASSWORD_LENGTH = 8;
    // 最近几次重复密码校验
    public static Integer HISTORY_SIZE = 0;
    // 校验首次登陆
    public static Boolean FIRST_LOGIN_CHECK = true;
    // 账号锁定阈值
    public static Integer LOCK_THRESHOLD = 10;
    // 验证码验证阈值
    public static Integer CHECK_THRESHOLD = 3;
    // 创建用户默认分配机构代码列表
    public static List<String> DEFAULT_USER_ORG_LIST = new ArrayList<>();
    // 租户默认开通产品列表
    public static List<String> DEFAULT_TENANT_PRODUCT = new ArrayList<>();

}
