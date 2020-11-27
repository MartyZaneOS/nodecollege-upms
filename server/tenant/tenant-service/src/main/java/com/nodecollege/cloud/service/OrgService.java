package com.nodecollege.cloud.service;

import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateAdminOrg;
import com.nodecollege.cloud.common.model.po.TenantOrg;
import com.nodecollege.cloud.common.model.po.TenantRoleOrg;
import com.nodecollege.cloud.common.model.po.TenantMemberOrg;

import java.util.List;

/**
 * @author LC
 * @date 2020/9/8 20:11
 */
public interface OrgService {
    /**
     * 获取机构列表
     */
    List<TenantOrg> getOrgList(QueryVO<TenantOrg> queryVO);

    /**
     * 添加机构
     */
    TenantOrg addOrg(TenantOrg org);

    /**
     * 编辑机构
     */
    void editOrg(TenantOrg org);

    /**
     * 删除机构
     */
    void delOrg(TenantOrg org);

    /**
     * 根据角色信息查询组织机构列表
     */
    List<TenantOrg> getOrgListByRole(QueryVO<TenantRoleOrg> queryVO);

    /**
     * 根据用户查询组织机构信息
     */
    List<TenantOrg> getOrgListByMember(QueryVO<TenantMemberOrg> queryVO);
}
