package com.nodecollege.cloud.service.impl;

import com.github.pagehelper.PageHelper;
import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.BindVO;
import com.nodecollege.cloud.common.model.MenuVO;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.*;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.common.utils.RedisUtils;
import com.nodecollege.cloud.cache.ConfigCache;
import com.nodecollege.cloud.dao.mapper.OperateTenantMapper;
import com.nodecollege.cloud.dao.mapper.OperateTenantProductMapper;
import com.nodecollege.cloud.dao.mapper.OperateUserMapper;
import com.nodecollege.cloud.dao.mapper.OperateUserTenantMapper;
import com.nodecollege.cloud.service.ProductMenuService;
import com.nodecollege.cloud.service.RoleService;
import com.nodecollege.cloud.service.TenantService;
import com.nodecollege.cloud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 租户管理service
 *
 * @author LC
 * @date 2019/8/4 10:21
 */
@Service
public class TenantServiceImpl implements TenantService {

    @Autowired
    private OperateTenantMapper tenantMapper;

    @Autowired
    private OperateUserMapper operateUserMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private OperateTenantProductMapper tenantProductMapper;

    @Autowired
    private OperateUserTenantMapper userTenantMapper;

    @Autowired
    private ProductMenuService productMenuService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 获取所有Tenant信息
     */
    @Override
    public List<OperateTenant> getTenantList(QueryVO<OperateTenant> query) {
        return tenantMapper.selectTenantListByMap(query.toMap());
    }

    /**
     * 根据用户id查询租户列表
     */
    @Override
    public List<OperateTenant> getTenantListByUserId(OperateUserTenant query) {
        return tenantMapper.selectTenantListByUserId(query.getUserId());
    }

    /**
     * 保存Tenant
     *
     * @param tenant
     * @return
     */
    @Override
    public OperateTenant addTenant(OperateTenant tenant) {

        NCUtils.nullOrEmptyThrow(tenant.getTenantCode(), new NCException("-1", "保存租户失败，租户代码必填！"));
        NCUtils.nullOrEmptyThrow(tenant.getTenantName(), new NCException("-1", "保存租户失败，租户名称必填！"));
        NCUtils.nullOrEmptyThrow(tenant.getCreateUserId(), new NCException("-1", "保存租户失败，创建用户id必填！"));
        OperateUser operateUser = userService.getUserByUserId(tenant.getCreateUserId());
        NCUtils.nullOrEmptyThrow(operateUser, new NCException("-1", "保存租户失败，创建者用户不存在！"));

        OperateTenant query = new OperateTenant();
        query.setTenantCode(tenant.getTenantCode());
        List<OperateTenant> existTenants = tenantMapper.selectTenantListByMap(new QueryVO<>(query).toMap());
        // tenant代码重复性检测
        NCUtils.notNullOrNotEmptyThrow(existTenants, new NCException("-1", "保存租户失败，该租户代码已存在！"));
        query.setTenantCode(null);
        query.setTenantName(tenant.getTenantName());
        existTenants = tenantMapper.selectTenantListByMap(new QueryVO<>(query).toMap());
        // tenant名称重复性检测
        NCUtils.notNullOrNotEmptyThrow(existTenants, new NCException("-1", "保存租户失败，该租户名称已存在！"));
        tenant.setState(0);
        tenant.setCreateTime(new Date());
        tenantMapper.insertSelective(tenant);

        // 添加默认产品
        for (String productCode : ConfigCache.DEFAULT_TENANT_PRODUCT) {
            OperateTenantProduct addTP = new OperateTenantProduct();
            addTP.setTenantId(tenant.getTenantId());
            addTP.setProductCode(productCode);
            addTP.setState(1);
            addTP.setCreateUser(tenant.getCreateUser());
            addTP.setCreateTime(new Date());
            tenantProductMapper.insertSelective(addTP);
        }

        // 默认租户为空，则设置新增租户为默认租户
        if (operateUser.getTenantId() == null) {
            operateUser.setTenantId(tenant.getTenantId());
            operateUser.setTenantCode(tenant.getTenantCode());
            operateUserMapper.updateByPrimaryKeySelective(operateUser);
        }

        // 添加用户租户关系
        OperateUserTenant userTenant = new OperateUserTenant();
        userTenant.setTenantId(tenant.getTenantId());
        userTenant.setUserId(operateUser.getUserId());
        userTenant.setState(1);
        userTenant.setCreateTime(new Date());
        userTenant.setCreateUser(tenant.getCreateUser());
        userTenantMapper.insertSelective(userTenant);

        // 新建该租户的顶级组织机构
//        UpmsOrg topOrg = new UpmsOrg();
//        topOrg.setOrgCode(tenant.getTenantCode());
//        topOrg.setOrgName(tenant.getTenantName());
//        topOrg.setOrgType(0);
//        topOrg.setParentId(0L);
//        topOrg.setNum(0);
//        topOrg.setState(0);
//        topOrg.setTenantId(tenant.getId());
//        upmsOrgMapper.insertSelective(topOrg);

        // 新建该租户的新成员组织机构 租户新增成员默认组织机构
//        UpmsOrg newMemberOrg = new UpmsOrg();
//        newMemberOrg.setOrgCode(UpmsConstants.NEW_MEMBER_DEFALUT_ORG);
//        newMemberOrg.setOrgName("新成员默认组织");
//        newMemberOrg.setOrgType(0);
//        newMemberOrg.setParentId(topOrg.getOrgId());
//        newMemberOrg.setNum(0);
//        newMemberOrg.setState(0);
//        newMemberOrg.setTenantId(tenant.getId());
//        upmsOrgMapper.insertSelective(newMemberOrg);

        // 绑定新成员角色和新成员组织机构关系
//        UpmsRole newMemberRole = new UpmsRole();
//        newMemberRole.setRoleCode("newMemberDefaultRole");
//        newMemberRole = upmsRoleMapper.selectListByMap(new QueryVO<>(newMemberRole).toMap()).get(0);
//
//        UpmsRoleOrg newMemberRoleOrg = new UpmsRoleOrg();
//        newMemberRoleOrg.setRoleId(newMemberRole.getRoleId());
//        newMemberRoleOrg.setOrgId(newMemberOrg.getOrgId());
//        newMemberRoleOrg.setTenantId(tenant.getId());
//        newMemberRoleOrg.setState(0);
//        upmsRoleOrgMapper.insert(newMemberRoleOrg);

        // todo 查询默认分配的应用信息
//        QueryVO<OperateConfig> queryConfig = new QueryVO<>(new OperateConfig());
//        queryConfig.getData().setConfigType(0);
//        queryConfig.getData().setConfigCode(UpmsConstants.DEFAULT_APPS);
//        List<OperateConfig> defaultAppConfig = configService.getConfigList(queryConfig);
//        List<String> apps = Arrays.asList(defaultAppConfig.get(0).getConfigValue().split(","));
//        QueryVO queryApp = new QueryVO();
//        queryApp.setStringList(apps);
//        List<UpmsApp> defaultAppList = upmsAppMapper.selectListByMap(queryApp.toMap());

        // 绑定租户应用信息
//        for (int i = 0; i < defaultAppList.size(); i++) {
//            UpmsTenantApp tenantApp = new UpmsTenantApp();
//            tenantApp.setAppId(defaultAppList.get(i).getAppId());
//            tenantApp.setTenantId(tenant.getId());
//            tenantApp.setState(1);
//            upmsTenantAppMapper.insert(tenantApp);
//        }

        // 查询该租户的超级管理员角色
//        queryConfig.getData().setConfigCode(UpmsConstants.DEFAULT_ROLES);
//        List<OperateConfig> defaultRoleConfig = configService.getConfigList(queryConfig);
//        List<String> roles = Arrays.asList(defaultRoleConfig.get(0).getConfigValue().split(","));
//        QueryVO queryRole = new QueryVO();
//        queryRole.setStringList(roles);
//        List<UpmsRole> defaultRoleList = upmsRoleMapper.selectListByMap(queryRole.toMap());

        // 新建该租户的超级管理员
//        UpmsMember superMember = new UpmsMember();
//        superMember.setMemberAccount("admin");
//        superMember.setMemberName(StringUtils.isBlank(operateUser.getNickname()) ? operateUser.getAccount() : operateUser.getNickname());
//        superMember.setUserId(tenant.getCreateUserId());
//        superMember.setTenantId(tenant.getId());
//        superMember.setDefaultOrgId(topOrg.getOrgId());
//        superMember.setDefaultRoleId(defaultRoleList.get(0).getRoleId());
//        superMember.setState(0);
//        superMember.setCreateTime(new Date());
//        upmsMemberMapper.insertSelective(superMember);

        // 绑定超级管理员和顶级组织机构关系
//        UpmsOrgMember orgMember = new UpmsOrgMember();
//        orgMember.setOrgId(topOrg.getOrgId());
//        orgMember.setMemberId(superMember.getMemberId());
//        upmsOrgMemberMapper.insert(orgMember);

//        for (int i = 0; i < defaultRoleList.size(); i++) {
//            // 绑定超级管理员和分配角色关系
//            UpmsRoleMember roleMember = new UpmsRoleMember();
//            roleMember.setRoleId(defaultRoleList.get(i).getRoleId());
//            roleMember.setMemberId(superMember.getMemberId());
//            roleMember.setTenantId(tenant.getId());
//            roleMember.setState(0);
//            upmsRoleMemberMapper.insert(roleMember);
//
//            // 绑定顶级组织机构和分配角色关系
//            UpmsRoleOrg roleOrg = new UpmsRoleOrg();
//            roleOrg.setRoleId(defaultRoleList.get(i).getRoleId());
//            roleOrg.setOrgId(topOrg.getOrgId());
//            roleOrg.setTenantId(tenant.getId());
//            roleOrg.setState(0);
//            upmsRoleOrgMapper.insert(roleOrg);
//        }
        clearTenantCache();
        return tenant;
    }

    /**
     * 更新tenant信息
     */
    @Override
    public void updateTenant(OperateTenant tenant) {
        NCUtils.nullOrEmptyThrow(tenant.getTenantId());
        // 是否存在检测
        OperateTenant existTenant = tenantMapper.selectByPrimaryKey(tenant.getTenantId());
        NCUtils.nullOrEmptyThrow(existTenant, "-1", "修改租户失败，该租户不存在！");

        tenant.setCreateUserId(null);
        tenant.setTenantCode(null);
        tenant.setCreateTime(null);
        tenant.setState(null);
        tenantMapper.updateByPrimaryKeySelective(tenant);
        clearTenantCache();
    }

    @Override
    public List<OperateTenantProduct> getTenantProduct(OperateTenantProduct query) {
        NCUtils.nullOrEmptyThrow(query.getTenantId());
        return tenantProductMapper.selectList(query);
    }

    @Override
    public void bindTenantProduct(BindVO bindVO) {
        NCUtils.nullOrEmptyThrow(bindVO.getTenantId());

        OperateTenantProduct queryProduct = new OperateTenantProduct();
        queryProduct.setTenantId(bindVO.getTenantId());
        List<OperateTenantProduct> exList = tenantProductMapper.selectList(queryProduct);

        List<String> exCodeList = new ArrayList<>();
        exList.forEach(item -> {
            if (NCUtils.isNullOrEmpty(bindVO.getTargetCodes()) || !bindVO.getTargetCodes().contains(item.getProductCode())) {
                tenantProductMapper.deleteByPrimaryKey(item.getId());
            } else {
                exCodeList.add(item.getProductCode());
            }
        });
        if (NCUtils.isNullOrEmpty(bindVO.getTargetCodes())) {
            return;
        }
        bindVO.getTargetCodes().forEach(item -> {
            if (!exCodeList.contains(item)) {
                OperateTenantProduct add = new OperateTenantProduct();
                add.setProductCode(item);
                add.setTenantId(bindVO.getTenantId());
                add.setState(1);
                tenantProductMapper.insertSelective(add);
            }
        });
    }

    @Override
    public List<MenuVO> getTenantMenu(OperateTenant tenant) {
        NCUtils.nullOrEmptyThrow(tenant.getTenantId());
        OperateTenant ex = tenantMapper.selectByPrimaryKey(tenant.getTenantId());
        NCUtils.nullOrEmptyThrow(ex, "", "租户不存在！");

        OperateTenantProduct queryProduct = new OperateTenantProduct();
        queryProduct.setTenantId(tenant.getTenantId());
        List<OperateTenantProduct> productList = tenantProductMapper.selectList(queryProduct);
        Set<MenuVO> menuSet = new HashSet<>();
        for (OperateTenantProduct product : productList) {
            OperateProductMenu queryMenu = new OperateProductMenu();
            queryMenu.setProductCode(product.getProductCode());
            menuSet.addAll(productMenuService.getProductMenuList(queryMenu));
        }

        return new ArrayList<>(menuSet);
    }

    @Override
    public List<OperateRole> getTenantRole(OperateTenant tenant) {
        NCUtils.nullOrEmptyThrow(tenant.getTenantId());
        OperateTenant ex = tenantMapper.selectByPrimaryKey(tenant.getTenantId());
        NCUtils.nullOrEmptyThrow(ex, "", "租户不存在！");

        OperateTenantProduct queryProduct = new OperateTenantProduct();
        queryProduct.setTenantId(tenant.getTenantId());
        List<OperateTenantProduct> productList = tenantProductMapper.selectList(queryProduct);

        QueryVO<OperateRole> queryRole = new QueryVO<>(new OperateRole());
        queryRole.getData().setRoleUsage(2);
        queryRole.setStringList(productList.stream().map(item -> item.getProductCode()).collect(Collectors.toList()));
        return roleService.getRoleList(queryRole);
    }

    /**
     * 根据租户id查询租户数据
     */
    @Override
    public OperateTenant getTenantById(OperateTenant operateTenant) {
        NCUtils.nullOrEmptyThrow(operateTenant.getTenantId(), new NCException("", "租户id不能为空！"));
        return tenantMapper.selectByPrimaryKey(operateTenant.getTenantId());
    }

    @Override
    public OperateTenant getTenantInfo(OperateTenant query) {
        if (query.getTenantId() != null) {
            return tenantMapper.selectByPrimaryKey(query.getTenantId());
        }
        PageHelper.startPage(1, 1);
        List<OperateTenant> list = tenantMapper.selectTenantListByMap(new QueryVO<>(query).toMap());
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    /**
     * 获取所有租户信息缓存
     */
    @Override
    public List<OperateTenant> getAllTenantListCache() {
        String key = "upms:tenant:getAllTenantNameCache";
        List<OperateTenant> tenantList = redisUtils.getList(key, OperateTenant.class);
        if (tenantList == null) {
            List<OperateTenant> list = tenantMapper.selectTenantListByMap(null);
            tenantList = new ArrayList<>((int) Math.ceil(list.size() / 0.75) + 1);
            for (int i = 0; i < list.size(); i++) {
                OperateTenant cache = new OperateTenant();
                cache.setTenantId(list.get(i).getTenantId());
                cache.setTenantName(list.get(i).getTenantName());
                tenantList.add(cache);
            }
            redisUtils.set(key, tenantList, 60 * 60 * 2);
        }
        return tenantList;
    }

    /**
     * 清楚租户缓存
     */
    @Override
    public void clearTenantCache() {
        redisUtils.delete("upms:tenant:getAllTenantNameCache");
    }
}
