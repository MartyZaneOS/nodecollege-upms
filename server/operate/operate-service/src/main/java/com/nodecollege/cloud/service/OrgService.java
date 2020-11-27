package com.nodecollege.cloud.service;

import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateAdminOrg;
import com.nodecollege.cloud.common.model.po.OperateOrg;
import com.nodecollege.cloud.common.model.po.OperateRoleOrg;
import com.nodecollege.cloud.common.model.po.OperateUserOrg;

import java.util.List;

/**
 * @author LC
 * @date 2020/9/8 20:11
 */
public interface OrgService {
    /**
     * 获取机构列表
     */
    List<OperateOrg> getOrgList(QueryVO<OperateOrg> queryVO);

    /**
     * 添加机构
     */
    void addOrg(OperateOrg org);

    /**
     * 编辑机构
     */
    void editOrg(OperateOrg org);

    /**
     * 删除机构
     */
    void delOrg(OperateOrg org);

    /**
     * 根据角色信息查询组织机构列表
     */
    List<OperateOrg> getOrgListByRole(QueryVO<OperateRoleOrg> queryVO);

    /**
     * 根据管理员查询组织机构信息
     */
    List<OperateOrg> getOrgListByAdmin(QueryVO<OperateAdminOrg> queryVO);

    /**
     * 根据用户查询组织机构信息
     */
    List<OperateOrg> getOrgListByUser(QueryVO<OperateUserOrg> queryVO);
}
