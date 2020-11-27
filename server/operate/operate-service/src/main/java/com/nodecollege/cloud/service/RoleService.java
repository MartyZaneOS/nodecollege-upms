package com.nodecollege.cloud.service;

import com.nodecollege.cloud.common.model.BindVO;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.*;

import java.util.List;

/**
 * @author LC
 * @date 2020/8/31 20:39
 */
public interface RoleService {
    /**
     * 查询角色列表
     */
    List<OperateRole> getRoleList(QueryVO<OperateRole> queryVO);

    /**
     * 添加角色
     */
    void addRole(OperateRole role);

    /**
     * 编辑角色
     */
    void editRole(OperateRole role);

    /**
     * 删除角色
     */
    void delRole(OperateRole role);

    /**
     * 查询角色菜单列表
     */
    List<OperateRoleMenu> getRoleMenuList(QueryVO<OperateRoleMenu> queryVO);

    /**
     * 绑定角色菜单
     */
    void bindRoleMenu(BindVO bindVO);

    /**
     * 查询角色列表根据机构信息
     */
    List<OperateRole> getRoleListByOrg(QueryVO<OperateRoleOrg> queryVO);

    /**
     * 查询角色列表根据管理员信息
     */
    List<OperateRole> getRoleListByAdmin(QueryVO<OperateAdminOrgRole> queryVO);

    /**
     * 查询角色列表根据管理员信息
     */
    List<OperateRole> getRoleListByUser(QueryVO<OperateUserOrgRole> queryVO);
}
