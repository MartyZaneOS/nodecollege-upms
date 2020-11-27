package com.nodecollege.cloud.service;

import com.nodecollege.cloud.common.model.BindVO;
import com.nodecollege.cloud.common.model.MenuVO;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateRole;
import com.nodecollege.cloud.common.model.po.OperateTenant;
import com.nodecollege.cloud.common.model.po.OperateTenantProduct;
import com.nodecollege.cloud.common.model.po.OperateUserTenant;

import java.util.List;

/**
 * @author LC
 * @date 2019/6/19 17:36
 */
public interface TenantService {

    /**
     * 获取所有UpmsTenant信息
     */
    List<OperateTenant> getTenantList(QueryVO<OperateTenant> queryVO);

    /**
     * 根据用户id查询租户信息
     */
    List<OperateTenant> getTenantListByUserId(OperateUserTenant userTenant);

    /**
     * 保存UpmsTenant
     */
    OperateTenant addTenant(OperateTenant operateTenant);

    /**
     * 更新UpmsTenant信息
     */
    void updateTenant(OperateTenant operateTenant);

    List<OperateTenantProduct> getTenantProduct(OperateTenantProduct query);

    void bindTenantProduct(BindVO bindVO);

    OperateTenant getTenantById(OperateTenant operateTenant);

    /**
     * 获取所有租户信息缓存
     */
    List<OperateTenant> getAllTenantListCache();

    /**
     * 清楚浏览器缓存
     */
    void clearTenantCache();

    /**
     * 查询租户菜单列表
     */
    List<MenuVO> getTenantMenu(OperateTenant tenant);

    /**
     * 查询租户角色列表
     */
    List<OperateRole> getTenantRole(OperateTenant tenant);

    /**
     * 查询租户信息
     */
    OperateTenant getTenantInfo(OperateTenant query);
}
