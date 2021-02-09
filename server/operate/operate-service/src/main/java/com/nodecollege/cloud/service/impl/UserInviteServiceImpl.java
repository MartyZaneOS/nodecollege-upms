package com.nodecollege.cloud.service.impl;

import com.nodecollege.cloud.common.exception.NCException;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        NCUtils.nullOrEmptyThrow(inviteUser.getCreateUser());
        NCUtils.nullOrEmptyThrow(inviteUser.getTenantId());
        if (StringUtils.isBlank(inviteUser.getTelephone()) == StringUtils.isBlank(inviteUser.getEmail())) {
            throw new NCException("", "电话邮箱必填其一！");
        }

        // 查询是否已经邀请过
        QueryVO<OperateUserInvite> queryInvite = new QueryVO<>(new OperateUserInvite());
        queryInvite.getData().setTenantId(inviteUser.getTenantId());
        queryInvite.getData().setState(0);

        // 查询平台是否有该用户
        QueryVO<OperateUser> queryUser = new QueryVO<>(new OperateUser());
        List<OperateUser> userList = new ArrayList<>();
        OperateUser user = null;
        if (StringUtils.isNotBlank(inviteUser.getTelephone())) {
            queryInvite.getData().setTelephone(inviteUser.getTelephone());
            List<OperateUserInvite> exList = invitationMapper.selectListByMap(queryInvite.toMap());
            NCUtils.notNullOrNotEmptyThrow(exList, "", "该用户已经在邀请了！");

            queryUser.getData().setTelephone(inviteUser.getTelephone());
            userList = userMapper.selectUserListByMap(queryUser.toMap());
            if (!userList.isEmpty()) {
                user = userList.get(0);
            }
        }
        if (user == null && StringUtils.isNotBlank(inviteUser.getEmail())) {
            queryInvite.getData().setTelephone(null);
            queryInvite.getData().setEmail(inviteUser.getEmail());
            List<OperateUserInvite> exList = invitationMapper.selectListByMap(queryInvite.toMap());
            NCUtils.notNullOrNotEmptyThrow(exList, "", "该用户已经在邀请了！");

            queryUser.getData().setTelephone(null);
            queryUser.getData().setEmail(inviteUser.getEmail());
            userList = userMapper.selectUserListByMap(queryUser.toMap());
            if (!userList.isEmpty()) {
                user = userList.get(0);
            }
        }

        if (user != null) {
            // 默认租户为空，则设置新增租户为默认租户
            OperateTenant tenant = tenantMapper.selectByPrimaryKey(inviteUser.getTenantId());
            if (user.getTenantId() == null) {
                user.setTenantId(inviteUser.getTenantId());
                user.setTenantCode(tenant.getTenantCode());
                userMapper.updateByPrimaryKeySelective(user);
            }

            // 添加用户租户关系
            OperateUserTenant userTenant = new OperateUserTenant();
            userTenant.setTenantId(tenant.getTenantId());
            userTenant.setUserId(user.getUserId());
            userTenant.setState(1);
            userTenant.setCreateTime(new Date());
            userTenant.setCreateUser(tenant.getCreateUser());
            userTenantMapper.insertSelective(userTenant);
            user.setNickname(inviteUser.getUserName());
            user.setTenantId(inviteUser.getTenantId());
            return user;
        }

        inviteUser.setState(0);
        inviteUser.setCreateTime(new Date());
        invitationMapper.insertSelective(inviteUser);
        return null;
    }
}
