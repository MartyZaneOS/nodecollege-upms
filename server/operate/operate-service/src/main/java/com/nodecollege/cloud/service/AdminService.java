package com.nodecollege.cloud.service;

import com.nodecollege.cloud.common.model.*;
import com.nodecollege.cloud.common.model.po.OperateAdmin;
import com.nodecollege.cloud.common.model.po.OperateAdminOrg;
import com.nodecollege.cloud.common.model.po.OperateAdminOrgRole;
import com.nodecollege.cloud.common.model.vo.LoginVO;

import java.util.List;

/**
 * 管理员service
 *
 * @author LC
 * @date 2019/11/27 19:36
 */
public interface AdminService {

    /**
     * 查询管理员列表
     *
     * @param query
     * @return
     */
    NCResult<OperateAdmin> getAdminList(QueryVO<OperateAdmin> query);

    /**
     * 新增管理员
     *
     * @param loginVO
     */
    OperateAdmin addAdmin(LoginVO loginVO);

    /**
     * 更新管理员信息
     *
     * @param operateAdmin
     */
    void updateAdmin(OperateAdmin operateAdmin);

    /**
     * 修改密码
     *
     * @param account   管理员账号
     * @param oldPassword   旧密码
     * @param newPassword   新密码
     */
    void updateAdminPassword(String account, String oldPassword, String newPassword, String cert);

    /**
     * 根据账号获取用户信息
     */
    OperateAdmin getAdminByAccount(String account);

    /**
     * 删除管理员
     */
    void delAdmin(Long id);

    /**
     * 冻结/解冻管理员
     */
    void lockAdmin(OperateAdmin operateAdmin);

    /**
     * 重置管理员密码
     */
    void resetPwd(OperateAdmin admin);

    void bindAdminOrg(BindVO bindVO);

    /**
     * 查询管理员列表，根据授权机构
     */
    List<OperateAdmin> getAdminListByOrg(QueryVO<OperateAdminOrg> queryVO);

    /**
     * 查询管理员列表，根据授权角色机构
     */
    List<OperateAdmin> getAdminListByRoleOrg(OperateAdminOrgRole queryVO);

    /**
     * 解绑管理员机构角色
     */
    void addAdminOrgRole(OperateAdminOrgRole bindVO);

    /**
     * 解绑管理员机构角色
     */
    void delAdminOrgRole(OperateAdminOrgRole bindVO);

    /**
     * 获取管理员授权信息
     */
    PowerVO getAdminPower(OperateAdmin admin);

    // 修改管理员默认机构角色选项
    NCLoginUserVO changeDefaultOption(String accessToken, OperateAdmin admin);
}
