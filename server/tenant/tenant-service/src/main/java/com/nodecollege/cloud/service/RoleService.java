package com.nodecollege.cloud.service;

import com.nodecollege.cloud.common.model.BindVO;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.TenantMemberOrgRole;
import com.nodecollege.cloud.common.model.po.TenantRole;
import com.nodecollege.cloud.common.model.po.TenantRoleMenu;
import com.nodecollege.cloud.common.model.po.TenantRoleOrg;

import java.util.List;

/**
 * @author LC
 * @date 2020/8/31 20:39
 */
public interface RoleService {
    /**
     * 查询角色列表
     */
    List<TenantRole> getRoleList(QueryVO<TenantRole> queryVO);

    /**
     * 添加角色
     */
    void addRole(TenantRole role);

    /**
     * 编辑角色
     */
    void editRole(TenantRole role);

    /**
     * 删除角色
     */
    void delRole(TenantRole role);

    /**
     * 查询角色菜单列表
     */
    List<TenantRoleMenu> getRoleMenuList(QueryVO<TenantRoleMenu> queryVO);

    /**
     * 绑定角色菜单
     */
    void bindRoleMenu(BindVO bindVO);

    /**
     * 查询角色列表根据机构信息
     */
    List<TenantRole> getRoleListByOrg(QueryVO<TenantRoleOrg> queryVO);

    /**
     * 绑定角色机构
     * mainSource true-自定义角色，false-系统角色
     */
    void bindRoleOrg(BindVO bindVO);

    /**
     * 查询角色列表根据管理员信息
     */
    List<TenantRole> getRoleListByMember(QueryVO<TenantMemberOrgRole> queryVO);
}
