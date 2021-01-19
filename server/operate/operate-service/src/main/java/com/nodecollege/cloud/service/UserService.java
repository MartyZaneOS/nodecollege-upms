package com.nodecollege.cloud.service;

import com.nodecollege.cloud.common.model.*;
import com.nodecollege.cloud.common.model.po.OperateUser;
import com.nodecollege.cloud.common.model.po.OperateUserOrg;
import com.nodecollege.cloud.common.model.po.OperateUserOrgRole;
import com.nodecollege.cloud.common.model.po.OperateUserTenant;
import com.nodecollege.cloud.common.model.vo.LoginVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * userService
 *
 * @author LC
 * @date 2019/6/13 15:49
 */
public interface UserService {

    /**
     * 查询所有用户
     */
    NCResult<OperateUser> list(QueryVO<OperateUser> userQuery);

    /**
     * 根据账号获取用户信息
     */
    OperateUser getUserByAccount(String account);

    /**
     * 用户注册/保存
     */
    void addUser(LoginVO loginVO);

    /**
     * 更新用户信息
     */
    NCLoginUserVO updateUser(OperateUser user, String userAccessToken);

    /**
     * 删除用户信息
     */
    void delUser(Long id);

    /**
     * 重置密码
     */
    void resetPwd(OperateUser operateUser);

    /**
     * 修改密码，只能当前登陆人修改自己的密码
     */
    void updatePwd(Long userId, String oldPassword, String newPassword);

    /**
     * 切换默认租户
     */
    void changeDefaultTenant(OperateUserTenant loginUser);

    /**
     * 查询用户信息
     */
    NCResult<OperateUser> getUserListByNickname(QueryVO<OperateUser> queryVO);

    /**
     * 根据用户id查询用户信息
     */
    List<OperateUser> getUserListByQuery(QueryVO queryVO);

    /**
     * 根据用户id获取用户信息
     */
    OperateUser getUserByUserId(Long userId);

    /**
     * 清除用户缓存
     */
    void clearUserCache(Long userId);

    /**
     * 上传头像
     */
    OperateUser uploadAvatar(MultipartFile avatarFile, Long userId);

    void bindUserOrg(BindVO bindVO);

    List<OperateUser> getUserListByOrg(QueryVO<OperateUserOrg> queryVO);

    List<OperateUser> getUserListByRoleOrg(OperateUserOrgRole queryVO);

    void addUserOrgRole(OperateUserOrgRole bindVO);

    void delUserOrgRole(OperateUserOrgRole bindVO);

    /**
     * 获取用户授权信息
     */
    PowerVO getUserPower(OperateUser user);

    NCLoginUserVO changeDefaultOption(String accessToken, OperateUser user);
}
