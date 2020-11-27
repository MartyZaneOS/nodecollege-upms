package com.nodecollege.cloud.service.impl;

import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateTenant;
import com.nodecollege.cloud.common.model.po.OperateUser;
import com.nodecollege.cloud.common.model.po.OperateUserInvite;
import com.nodecollege.cloud.common.model.po.OperateUserTenant;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.dao.mapper.OperateTenantMapper;
import com.nodecollege.cloud.dao.mapper.OperateUserInviteMapper;
import com.nodecollege.cloud.dao.mapper.OperateUserMapper;
import com.nodecollege.cloud.dao.mapper.OperateUserTenantMapper;
import com.nodecollege.cloud.service.UserInviteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 邀请用户
 *
 * @author LC
 * @date 2019/12/12 18:15
 */
@Service
public class UserInviteServiceImpl implements UserInviteService {

    @Autowired
    private OperateUserMapper userMapper;

    @Autowired
    private OperateUserInviteMapper invitationMapper;

    @Autowired
    private OperateUserTenantMapper userTenantMapper;

    @Autowired
    private OperateTenantMapper tenantMapper;

    /**
     * 查询邀请列表
     */
    @Override
    public List<OperateUserInvite> getInvitedList(QueryVO<OperateUserInvite> queryVO) {
        return invitationMapper.selectListByMap(queryVO.toMap());
    }

    /**
     * 邀请用户
     */
    @Override
    public OperateUser inviteMember(OperateUserInvite inviteUser) {
        NCUtils.nullOrEmptyThrow(inviteUser.getTelephone());
        NCUtils.nullOrEmptyThrow(inviteUser.getCreateUser());
        NCUtils.nullOrEmptyThrow(inviteUser.getTenantId());

        QueryVO<OperateUserInvite> query = new QueryVO<>(new OperateUserInvite());
        query.getData().setTenantId(inviteUser.getTenantId());
        query.getData().setTelephone(inviteUser.getTelephone());
        query.getData().setState(0);
        List<OperateUserInvite> exList = invitationMapper.selectListByMap(query.toMap());
        NCUtils.notNullOrNotEmptyThrow(exList, "", "该用户已经在邀请了！");

        QueryVO<OperateUser> queryUser = new QueryVO<>(new OperateUser());
        queryUser.getData().setTelephone(inviteUser.getTelephone());
        List<OperateUser> userList = userMapper.selectUserListByMap(queryUser.toMap());
        if (!userList.isEmpty()) {
            OperateUser operateUser = userList.get(0);
            // 默认租户为空，则设置新增租户为默认租户
            OperateTenant tenant = tenantMapper.selectByPrimaryKey(inviteUser.getTenantId());
            if (operateUser.getTenantId() == null) {
                operateUser.setTenantId(inviteUser.getTenantId());
                operateUser.setTenantCode(tenant.getTenantCode());
                userMapper.updateByPrimaryKeySelective(operateUser);
            }

            // 添加用户租户关系
            OperateUserTenant userTenant = new OperateUserTenant();
            userTenant.setTenantId(tenant.getTenantId());
            userTenant.setUserId(operateUser.getUserId());
            userTenant.setState(1);
            userTenant.setCreateTime(new Date());
            userTenant.setCreateUser(tenant.getCreateUser());
            userTenantMapper.insertSelective(userTenant);
            operateUser.setNickname(inviteUser.getUserName());
            operateUser.setTenantId(inviteUser.getTenantId());
            return operateUser;
        }

        inviteUser.setState(0);
        inviteUser.setCreateTime(new Date());
        invitationMapper.insertSelective(inviteUser);
        return null;
    }
}
