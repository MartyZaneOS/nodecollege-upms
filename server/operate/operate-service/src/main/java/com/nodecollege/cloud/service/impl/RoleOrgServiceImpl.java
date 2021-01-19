package com.nodecollege.cloud.service.impl;

import com.nodecollege.cloud.common.model.BindVO;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateAdminOrgRole;
import com.nodecollege.cloud.common.model.po.OperateRole;
import com.nodecollege.cloud.common.model.po.OperateRoleOrg;
import com.nodecollege.cloud.common.model.po.OperateUserOrgRole;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.dao.mapper.OperateAdminOrgRoleMapper;
import com.nodecollege.cloud.dao.mapper.OperateRoleMapper;
import com.nodecollege.cloud.dao.mapper.OperateRoleOrgMapper;
import com.nodecollege.cloud.dao.mapper.OperateUserOrgRoleMapper;
import com.nodecollege.cloud.service.RoleOrgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LC
 * @date 2020/9/8 22:27
 */
@Service
public class RoleOrgServiceImpl implements RoleOrgService {

    @Autowired
    private OperateRoleOrgMapper orgRoleMapper;

    @Autowired
    private OperateRoleMapper roleMapper;

    @Autowired
    private OperateUserOrgRoleMapper userOrgRoleMapper;

    @Autowired
    private OperateAdminOrgRoleMapper adminOrgRoleMapper;

    @Override
    public void bindRoleOrg(BindVO bindVO) {
        NCUtils.nullOrEmptyThrow(bindVO.getSourceCodes());
        NCUtils.nullOrEmptyThrow(bindVO.getBindUsage());

        QueryVO<OperateRole> queryRole = new QueryVO<>(new OperateRole());
        queryRole.getData().setRoleCode(bindVO.getSourceCodes().get(0));
        queryRole.getData().setRoleUsage(bindVO.getBindUsage());
        List<OperateRole> roleList = roleMapper.selectListByMap(queryRole.toMap());
        NCUtils.nullOrEmptyThrow(roleList, "", "角色不存在！");

        Integer roleType = roleList.get(0).getRoleType();

        // 已绑定的数据
        OperateRoleOrg query = new OperateRoleOrg();
        query.setRoleOrgUsage(bindVO.getBindUsage());
        query.setRoleCode(bindVO.getSourceCodes().get(0));

        List<String> exCodeList = new ArrayList<>();
        List<OperateRoleOrg> exList = orgRoleMapper.selectList(query);
        exList.forEach(item -> {
            if (NCUtils.isNullOrEmpty(bindVO.getTargetCodes()) || !bindVO.getTargetCodes().contains(item.getOrgCode())) {
                if (roleType == 1){
                    if (bindVO.getBindUsage() == 0) {
                        OperateAdminOrgRole queryAdmin = new OperateAdminOrgRole();
                        queryAdmin.setRoleCode(bindVO.getSourceCodes().get(0));
                        queryAdmin.setOrgCode(item.getOrgCode());
                        List<OperateAdminOrgRole> adminList = adminOrgRoleMapper.selectList(queryAdmin);
                        NCUtils.notNullOrNotEmptyThrow(adminList, "", item.getOrgCode() + "机构下有绑定管理员信息！不能解绑！");
                    } else {
                        OperateUserOrgRole queryUser = new OperateUserOrgRole();
                        queryUser.setRoleCode(bindVO.getSourceCodes().get(0));
                        queryUser.setOrgCode(item.getOrgCode());
                        List<OperateUserOrgRole> userList = userOrgRoleMapper.selectList(queryUser);
                        NCUtils.notNullOrNotEmptyThrow(userList, "", item.getOrgCode() + "机构下有绑定用户信息！不能解绑！");
                    }
                }
                orgRoleMapper.deleteByPrimaryKey(item.getId());
            } else {
                exCodeList.add(item.getOrgCode());
            }
        });
        if (NCUtils.isNullOrEmpty(bindVO.getSourceCodes())) {
            return;
        }
        bindVO.getTargetCodes().forEach(item -> {
            if (!exCodeList.contains(item)) {
                OperateRoleOrg add = new OperateRoleOrg();
                add.setRoleOrgUsage(bindVO.getBindUsage());
                add.setOrgCode(item);
                add.setRoleCode(bindVO.getSourceCodes().get(0));
                orgRoleMapper.insertSelective(add);
            }
        });
    }
}
