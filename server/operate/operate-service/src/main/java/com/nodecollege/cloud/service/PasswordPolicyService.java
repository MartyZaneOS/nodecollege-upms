package com.nodecollege.cloud.service;

import com.nodecollege.cloud.common.model.po.OperateAdmin;
import com.nodecollege.cloud.common.model.po.OperateAdminPassword;
import com.nodecollege.cloud.common.model.po.OperateUserPassword;

import java.util.List;

/**
 * 密码策略
 *
 * @author LC
 * @date 2019/12/1 13:19
 */
public interface PasswordPolicyService {

    /**
     * 校验登陆失败密码策略
     *
     * @param userName    用户名称
     * @param password    明文用户密码
     * @param newPassword 明文新用户密码
     * @param cert        验证证书
     */
    void checkLoginPolicy(String userName, String password, String newPassword, String cert);

    /**
     * 校验登陆失败密码策略
     *
     * @param userName    用户名称
     * @param password    明文用户密码
     * @param newPassword 明文新用户密码
     * @param cert        验证证书
     */
    void checkUserLoginPolicy(String userName, String password, String newPassword, String cert);

    /**
     * 登陆成功后校验密码策略
     *
     * @param admin
     * @param passwordList
     */
    void checkLoginSuccessPolicy(OperateAdmin admin, List<OperateAdminPassword> passwordList, String newPassword, String cert);

    /**
     * 登陆成功后校验密码策略
     *
     * @param userId
     * @param firstLogin
     */
    Boolean checkUserLoginSuccessPolicy(Long userId, Integer firstLogin);

    /**
     * 校验注册密码策略
     *
     * @param password 明文用户密码
     * @param account  用户名称/电话
     */
    void checkRegisterPolicy(String password, String account);

    /**
     * 校验更新用户密码策略
     *
     * @param list        历史密码信息
     * @param newPassword 明文新密码
     */
    void checkUpdatePolicy(List<OperateAdminPassword> list, String newPassword);

    /**
     * 校验更新用户密码策略
     *
     * @param list        历史密码信息
     * @param newPassword 明文新密码
     */
    void checkUpdatePolicyUser(List<OperateUserPassword> list, String newPassword);

    /**
     * 设置登陆失败次数
     *
     * @param account
     */
    Integer setLoginFileNum(String account);

    /**
     * 设置登陆失败次数
     *
     * @param account
     */
    Integer setUserLoginFileNum(String account);

    /**
     * 清除失败次数
     * @param account
     */
    void deleteLoginFileNum(String account);

    /**
     * 清除失败次数
     * @param account
     */
    void deleteUserLoginFileNum(String account);
}
