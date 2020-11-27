package com.nodecollege.cloud.service.impl;

import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.BindVO;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.*;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.dao.mapper.*;
import com.nodecollege.cloud.service.RoleService;
import com.nodecollege.cloud.feign.TenantClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author LC
 * @date 2020/8/31 20:40
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private TenantRoleMapper roleMapper;

    @Autowired
    private TenantRoleMenuMapper roleMenuMapper;

    @Autowired
    private TenantRoleOrgMapper roleOrgMapper;

    @Autowired
    private TenantOrgMapper orgMapper;

    @Autowired
    private TenantMemberOrgRoleMapper memberOrgRoleMapper;

    @Autowired
    private TenantClient tenantClient;

    @Override
    public List<TenantRole> getRoleList(QueryVO<TenantRole> queryVO) {
        NCUtils.nullOrEmptyThrow(queryVO.getData().getTenantId());

        List<TenantRole> result = new ArrayList<>();
        // 查询预制角色
        OperateTenant querySysRole = new OperateTenant();
        querySysRole.setTenantId(queryVO.getData().getTenantId());
        List<OperateRole> sysList = tenantClient.getTenantRole(querySysRole).getRows();
        for (OperateRole role : sysList) {
            TenantRole temp = new TenantRole();
            temp.setRoleCode(role.getRoleCode());
            temp.setRoleName(role.getRoleName());
            temp.setRoleDesc(role.getRoleDesc());
            temp.setRoleType(role.getRoleType());
            temp.setDataPower(role.getDataPower());
            temp.setRoleSource(role.getRoleState());
            temp.setRoleSource(0);
            result.add(temp);
        }

        // 查询自定义角色
        List<TenantRole> roleList = roleMapper.selectListByMap(queryVO.toMap());
        for (TenantRole role : roleList) {
            role.setRoleSource(1);
            result.add(role);
        }
        return result;
    }

    @Override
    public void addRole(TenantRole role) {
        NCUtils.nullOrEmptyThrow(role.getRoleCode());
        NCUtils.nullOrEmptyThrow(role.getRoleName());
        NCUtils.nullOrEmptyThrow(role.getRoleType());
        NCUtils.nullOrEmptyThrow(role.getDataPower());

        if (role.getRoleCode().indexOf("t_") == 0) {
            throw new NCException("", "自定义角色不能以\"t_\"为开头");
        }

        QueryVO<TenantRole> query = new QueryVO<>(new TenantRole());
        query.getData().setRoleCode(role.getRoleCode());
        query.getData().setTenantId(role.getTenantId());
        List<TenantRole> exRole = roleMapper.selectListByMap(query.toMap());
        NCUtils.notNullOrNotEmptyThrow(exRole, "", "角色代码已存在！");

        role.setRoleState(0);
        roleMapper.insertSelective(role);
    }

    @Override
    public void editRole(TenantRole role) {
        NCUtils.nullOrEmptyThrow(role.getId());
        NCUtils.nullOrEmptyThrow(role.getTenantId());
        TenantRole ex = roleMapper.selectByPrimaryKey(role.getId());
        NCUtils.nullOrEmptyThrow(ex, "", "角色不存在！");

        if (!role.getTenantId().equals(ex.getTenantId())) {
            throw new NCException("", "角色不存在！");
        }

        role.setRoleCode(null);
        role.setUpdateTime(new Date());
        role.setUpdateUser(role.getUpdateUser());

        roleMapper.updateByPrimaryKeySelective(role);
    }

    @Override
    public void delRole(TenantRole role) {
        NCUtils.nullOrEmptyThrow(role.getId());
        TenantRole ex = roleMapper.selectByPrimaryKey(role.getId());
        NCUtils.nullOrEmptyThrow(ex, "", "角色不存在！");
        if (!role.getTenantId().equals(ex.getTenantId())) {
            throw new NCException("", "不能删除该用途角色！");
        }

        // 查询绑定菜单
        QueryVO<TenantRoleMenu> querySource = new QueryVO<>(new TenantRoleMenu());
        querySource.getData().setRoleCode(role.getRoleCode());
        querySource.getData().setTenantId(role.getTenantId());
        List<TenantRoleMenu> exList = roleMenuMapper.selectListByMap(querySource.toMap());
        NCUtils.notNullOrNotEmptyThrow(exList, "", "存在绑定菜单，不能删除！");

        // 查询绑定组织机构
        TenantRoleOrg queryOrg = new TenantRoleOrg();
        queryOrg.setTenantId(role.getTenantId());
        queryOrg.setRoleCode(role.getRoleCode());
        List<TenantRoleOrg> exOrgList = roleOrgMapper.selectList(queryOrg);
        NCUtils.notNullOrNotEmptyThrow(exOrgList, "", "存在绑定机构，不能删除！");

        TenantRole update = new TenantRole();
        update.setId(ex.getId());
        update.setRoleState(-1);
        roleMapper.updateByPrimaryKeySelective(update);
    }

    @Override
    public List<TenantRoleMenu> getRoleMenuList(QueryVO<TenantRoleMenu> queryVO) {
        NCUtils.nullOrEmptyThrow(queryVO.getData().getRoleCode());

        List<TenantRoleMenu> list = new ArrayList<>();
        if (queryVO.getData().getRoleCode().indexOf("t_") == 0) {
            // 查询预制角色菜单列表
            QueryVO<OperateRoleMenu> queryMenu = new QueryVO(new OperateRoleMenu());
            queryMenu.getData().setRoleCode(queryVO.getData().getRoleCode());
            List<OperateRoleMenu> roleMenuList = tenantClient.getTenantRoleMenuList(queryMenu).getRows();
            for (OperateRoleMenu menu: roleMenuList) {
                TenantRoleMenu add = new TenantRoleMenu();
                add.setRoleCode(menu.getRoleCode());
                add.setMenuCode(menu.getMenuCode());
                add.setRoleSource(0);
                add.setTenantId(queryVO.getData().getTenantId());
                list.add(add);
            }
        } else {
            list = roleMenuMapper.selectListByMap(queryVO.toMap());
        }
        return list;
    }

    @Override
    public void bindRoleMenu(BindVO bindVO) {
        NCUtils.nullOrEmptyThrow(bindVO.getSourceCodes());
        QueryVO<TenantRoleMenu> querySource = new QueryVO<>(new TenantRoleMenu());
        querySource.getData().setRoleCode(bindVO.getSourceCodes().get(0));
        querySource.getData().setTenantId(bindVO.getTenantId());
        List<TenantRoleMenu> exList = roleMenuMapper.selectListByMap(querySource.toMap());

        List<String> exCodeList = new ArrayList<>();
        exList.forEach(item -> {
            if (NCUtils.isNullOrEmpty(bindVO.getTargetCodes()) || !bindVO.getTargetCodes().contains(item.getMenuCode())) {
                roleMenuMapper.deleteByPrimaryKey(item.getId());
            } else {
                exCodeList.add(item.getMenuCode());
            }
        });
        if (NCUtils.isNullOrEmpty(bindVO.getTargetCodes())) {
            return;
        }
        bindVO.getTargetCodes().forEach(item -> {
            if (!exCodeList.contains(item)) {
                TenantRoleMenu add = new TenantRoleMenu();
                add.setTenantId(bindVO.getTenantId());
                add.setRoleCode(bindVO.getSourceCodes().get(0));
                add.setMenuCode(item);
                roleMenuMapper.insert(add);
            }
        });
    }

    @Override
    public List<TenantRole> getRoleListByOrg(QueryVO<TenantRoleOrg> queryVO) {
        NCUtils.nullOrEmptyThrow(queryVO.getData());
        NCUtils.nullOrEmptyThrow(queryVO.getData().getOrgCode());
        NCUtils.nullOrEmptyThrow(queryVO.getData().getTenantId());

        QueryVO<TenantRole> queryRole = new QueryVO<>(new TenantRole());
        queryRole.getData().setTenantId(queryVO.getData().getTenantId());
        List<TenantRole> list = getRoleList(queryRole);

        List<TenantRoleOrg> roleOrgList = roleOrgMapper.selectList(queryVO.getData());
        Map<String, Integer> roleMap = new HashMap<>();
        roleOrgList.forEach(item -> roleMap.put(item.getRoleCode(), 1));

        List<TenantRole> returnList = new ArrayList<>();
        for (TenantRole role : list) {
            if (roleMap.containsKey(role.getRoleCode())) {
                returnList.add(role);
            }
        }

        return returnList;
    }

    @Override
    public void bindRoleOrg(BindVO bindVO) {
        NCUtils.nullOrEmptyThrow(bindVO.getSourceCodes());
        NCUtils.nullOrEmptyThrow(bindVO.getMainSource());

        TenantRoleOrg querySource = new TenantRoleOrg();
        querySource.setRoleCode(bindVO.getSourceCodes().get(0));
        querySource.setTenantId(bindVO.getTenantId());
        querySource.setRoleSource(bindVO.getMainSource() ? 1 : 0);
        List<TenantRoleOrg> exList = roleOrgMapper.selectList(querySource);

        List<String> exCodeList = new ArrayList<>();
        exList.forEach(item -> {
            if (NCUtils.isNullOrEmpty(bindVO.getTargetCodes()) || !bindVO.getTargetCodes().contains(item.getOrgCode())) {
                roleOrgMapper.deleteByPrimaryKey(item.getId());
            } else {
                exCodeList.add(item.getOrgCode());
            }
        });
        if (NCUtils.isNullOrEmpty(bindVO.getTargetCodes())) {
            return;
        }
        bindVO.getTargetCodes().forEach(item -> {
            if (!exCodeList.contains(item)) {
                TenantRoleOrg add = new TenantRoleOrg();
                add.setTenantId(bindVO.getTenantId());
                add.setRoleCode(bindVO.getSourceCodes().get(0));
                add.setOrgCode(item);
                add.setRoleSource(bindVO.getMainSource() ? 1 : 0);
                roleOrgMapper.insert(add);
            }
        });
    }

    @Override
    public List<TenantRole> getRoleListByMember(QueryVO<TenantMemberOrgRole> queryVO) {
        NCUtils.nullOrEmptyThrow(queryVO.getData().getTenantId());

        QueryVO<TenantMemberOrg> queryOrg = new QueryVO<>(new TenantMemberOrg());
        queryOrg.getData().setMemberId(queryVO.getData().getMemberId());
        List<TenantOrg> orgList = orgMapper.selectListByMember(queryOrg.toMap());

        // 查询所属机构拥有的机构角色
        Set<TenantRole> roleSet = new HashSet<>();
        QueryVO<TenantRoleOrg> queryRoleOrg = new QueryVO<>(new TenantRoleOrg());
        queryRoleOrg.getData().setTenantId(queryVO.getData().getTenantId());
        for (int i = 0; i < orgList.size(); i++) {
            queryRoleOrg.getData().setOrgCode(orgList.get(i).getOrgCode());
            List<TenantRole> exRoleList = getRoleListByOrg(queryRoleOrg);
            for (int j = 0; j < exRoleList.size(); j++) {
                // 添加所属机构拥有的机构角色
                if (exRoleList.get(j).getRoleType() == 0) {
                    roleSet.add(exRoleList.get(j));
                }
            }
        }

        // 查询管理员拥有的机构管理员角色
        TenantMemberOrgRole queryMOR = new TenantMemberOrgRole();
        queryMOR.setTenantId(queryVO.getData().getTenantId());
        queryMOR.setMemberId(queryVO.getData().getMemberId());
        List<TenantMemberOrgRole> morList = memberOrgRoleMapper.selectList(queryMOR);

        QueryVO<TenantRole> queryRole = new QueryVO<>(new TenantRole());
        queryRole.getData().setTenantId(queryVO.getData().getTenantId());
        List<TenantRole> list = getRoleList(queryRole);

        Map<String, Integer> roleMap = new HashMap<>();
        morList.forEach(item -> roleMap.put(item.getRoleCode(), 1));

        List<TenantRole> returnList = new ArrayList<>();
        for (TenantRole role : list) {
            if (roleMap.containsKey(role.getRoleCode())) {
                returnList.add(role);
            }
        }
        roleSet.addAll(returnList);

        return new ArrayList<>(roleSet);
    }
}
