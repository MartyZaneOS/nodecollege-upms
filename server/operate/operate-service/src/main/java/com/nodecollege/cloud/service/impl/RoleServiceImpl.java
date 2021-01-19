package com.nodecollege.cloud.service.impl;

import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.BindVO;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.*;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.dao.mapper.OperateOrgMapper;
import com.nodecollege.cloud.dao.mapper.OperateProductMapper;
import com.nodecollege.cloud.dao.mapper.OperateRoleMapper;
import com.nodecollege.cloud.dao.mapper.OperateRoleMenuMapper;
import com.nodecollege.cloud.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author LC
 * @date 2020/8/31 20:40
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private OperateRoleMapper roleMapper;

    @Autowired
    private OperateRoleMenuMapper roleMenuMapper;

    @Autowired
    private OperateProductMapper productMapper;

    @Autowired
    private OperateOrgMapper orgMapper;

    @Override
    public List<OperateRole> getRoleList(QueryVO<OperateRole> queryVO) {
        NCUtils.nullOrEmptyThrow(queryVO.getData().getRoleUsage());
        return roleMapper.selectListByMap(queryVO.toMap());
    }

    @Override
    public void addRole(OperateRole role) {
        NCUtils.nullOrEmptyThrow(role.getProductCode());
        NCUtils.nullOrEmptyThrow(role.getRoleCode());
        NCUtils.nullOrEmptyThrow(role.getRoleName());
        NCUtils.nullOrEmptyThrow(role.getRoleType());
        NCUtils.nullOrEmptyThrow(role.getDataPower());

        if (role.getRoleUsage() == 2 && role.getRoleCode().indexOf("t_") != 0) {
            throw new NCException("", "租户预制角色必须以\"t_\"为开头");
        }

        OperateProduct queryP = new OperateProduct();
        queryP.setProductCode(role.getProductCode());
        List<OperateProduct> exProList = productMapper.selectProductList(queryP);
        NCUtils.nullOrEmptyThrow(exProList, "", "产品不存在！");
        if (exProList.get(0).getProductType() != 1) {
            throw new NCException("", "只能挂在共存式产品下！");
        }
        // 和产品用途一致
        if (!role.getRoleUsage().equals(exProList.get(0).getProductUsage())) {
            throw new NCException("", "角色用途与产品用途不一致！");
        }
        QueryVO<OperateRole> query = new QueryVO<>(new OperateRole());
        query.getData().setRoleCode(role.getRoleCode());
        query.getData().setRoleUsage(role.getRoleUsage());
        List<OperateRole> exRole = roleMapper.selectListByMap(query.toMap());
        NCUtils.notNullOrNotEmptyThrow(exRole, "", "角色代码已存在！");

        role.setRoleState(0);
        roleMapper.insertSelective(role);
    }

    @Override
    public void editRole(OperateRole role) {
        NCUtils.nullOrEmptyThrow(role.getId());
        OperateRole ex = roleMapper.selectByPrimaryKey(role.getId());
        NCUtils.nullOrEmptyThrow(ex, "", "角色不存在！");
        if (!role.getRoleUsage().equals(ex.getRoleUsage())) {
            throw new NCException("", "不能操作该用途角色！");
        }
        role.setProductCode(null);
        role.setRoleCode(null);
        role.setRoleUsage(null);
        roleMapper.updateByPrimaryKeySelective(role);
    }

    @Override
    public void delRole(OperateRole role) {
        NCUtils.nullOrEmptyThrow(role.getId());
        OperateRole ex = roleMapper.selectByPrimaryKey(role.getId());
        NCUtils.nullOrEmptyThrow(ex, "", "角色不存在！");
        if (!role.getRoleUsage().equals(ex.getRoleUsage())) {
            throw new NCException("", "不能删除该用途角色！");
        }

        // 查询绑定菜单
        QueryVO<OperateRoleMenu> querySource = new QueryVO<>(new OperateRoleMenu());
        querySource.getData().setRoleCode(role.getRoleCode());
        List<OperateRoleMenu> exList = roleMenuMapper.selectListByMap(querySource.toMap());
        NCUtils.notNullOrNotEmptyThrow(exList, "", "存在绑定菜单，不能删除！");

        // todo 查询绑定用户
        if (ex.getRoleUsage() == 0) {
            // 运营角色
        } else if (ex.getRoleUsage() == 1) {
            // 2C角色
        } else if (ex.getRoleUsage() == 2) {
            // 2B角色
        }

        OperateRole update = new OperateRole();
        update.setId(ex.getId());
        update.setRoleState(-1);
        roleMapper.updateByPrimaryKeySelective(update);
    }

    @Override
    public List<OperateRoleMenu> getRoleMenuList(QueryVO<OperateRoleMenu> queryVO) {
        return roleMenuMapper.selectListByMap(queryVO.toMap());
    }

    @Override
    public void bindRoleMenu(BindVO bindVO) {
        NCUtils.nullOrEmptyThrow(bindVO.getSourceCodes());
        NCUtils.nullOrEmptyThrow(bindVO.getNavPlatform());
        QueryVO<OperateRoleMenu> querySource = new QueryVO<>(new OperateRoleMenu());
        querySource.getData().setRoleCode(bindVO.getSourceCodes().get(0));
        querySource.getData().setRoleMenuUsage(bindVO.getBindUsage());
        querySource.getData().setNavPlatform(bindVO.getNavPlatform());
        List<OperateRoleMenu> exList = roleMenuMapper.selectListByMap(querySource.toMap());

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
                OperateRoleMenu add = new OperateRoleMenu();
                add.setRoleMenuUsage(bindVO.getBindUsage());
                add.setRoleCode(bindVO.getSourceCodes().get(0));
                add.setMenuCode(item);
                add.setNavPlatform(bindVO.getNavPlatform());
                roleMenuMapper.insert(add);
            }
        });
    }

    @Override
    public List<OperateRole> getRoleListByOrg(QueryVO<OperateRoleOrg> queryVO) {
        NCUtils.nullOrEmptyThrow(queryVO.getData());
        NCUtils.nullOrEmptyThrow(queryVO.getData().getOrgCode());
        NCUtils.nullOrEmptyThrow(queryVO.getData().getRoleOrgUsage());
        return roleMapper.selectListByOrg(queryVO.toMap());
    }

    @Override
    public List<OperateRole> getRoleListByAdmin(QueryVO<OperateAdminOrgRole> queryVO) {
        NCUtils.nullOrEmptyThrow(queryVO.getData().getAdminId());

        QueryVO<OperateAdminOrg> queryOrg = new QueryVO<>(new OperateAdminOrg());
        queryOrg.getData().setAdminId(queryVO.getData().getAdminId());
        List<OperateOrg> orgList = orgMapper.selectListByAdmin(queryOrg.toMap());

        // 查询所属机构拥有的机构角色
        Set<OperateRole> roleSet = new HashSet<>();
        QueryVO<OperateRoleOrg> queryRole = new QueryVO<>(new OperateRoleOrg());
        queryRole.getData().setRoleOrgUsage(0);
        for (int i = 0; i < orgList.size(); i++) {
            queryRole.getData().setOrgCode(orgList.get(i).getOrgCode());
            List<OperateRole> exRoleList = getRoleListByOrg(queryRole);
            for (int j = 0; j < exRoleList.size(); j++) {
                // 添加所属机构拥有的机构角色
                if (exRoleList.get(j).getRoleType() == 0) {
                    roleSet.add(exRoleList.get(j));
                }
            }
        }

        // 查询管理员拥有的机构管理员角色
        List<OperateRole> roleList = roleMapper.selectListByAdmin(queryOrg.toMap());
        roleSet.addAll(roleList);

        return new ArrayList<>(roleSet);
    }

    @Override
    public List<OperateRole> getRoleListByUser(QueryVO<OperateUserOrgRole> queryVO) {
        NCUtils.nullOrEmptyThrow(queryVO.getData().getUserId());

        QueryVO<OperateUserOrg> queryOrg = new QueryVO<>(new OperateUserOrg());
        queryOrg.getData().setUserId(queryVO.getData().getUserId());
        List<OperateOrg> orgList = orgMapper.selectListByUser(queryOrg.toMap());

        // 查询所属机构拥有的机构角色
        Set<OperateRole> roleSet = new HashSet<>();
        QueryVO<OperateRoleOrg> queryRole = new QueryVO<>(new OperateRoleOrg());
        queryRole.getData().setRoleOrgUsage(1);
        for (int i = 0; i < orgList.size(); i++) {
            queryRole.getData().setOrgCode(orgList.get(i).getOrgCode());
            List<OperateRole> exRoleList = getRoleListByOrg(queryRole);
            for (int j = 0; j < exRoleList.size(); j++) {
                // 添加所属机构拥有的机构角色
                if (exRoleList.get(j).getRoleType() == 0) {
                    roleSet.add(exRoleList.get(j));
                }
            }
        }

        // 查询管理员拥有的机构管理员角色
        List<OperateRole> roleList = roleMapper.selectListByUser(queryOrg.toMap());
        roleSet.addAll(roleList);

        return new ArrayList<>(roleSet);
    }
}
