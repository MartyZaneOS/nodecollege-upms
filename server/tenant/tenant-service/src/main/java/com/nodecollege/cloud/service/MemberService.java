package com.nodecollege.cloud.service;

import com.nodecollege.cloud.common.model.*;
import com.nodecollege.cloud.common.model.po.*;
import com.nodecollege.cloud.common.model.vo.LoginVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * userService
 *
 * @author LC
 * @date 2019/6/13 15:49
 */
public interface MemberService {

    /**
     * 查询所有用户
     */
    List<TenantMember> list(QueryVO<TenantMember> userQuery);

    /**
     * 用户注册/保存
     */
    TenantMember addMember(TenantMember loginVO);

    /**
     * 更新用户信息
     */
    NCLoginUserVO updateMember(TenantMember user, String userAccessToken);

    /**
     * 删除用户信息
     */
    void delMember(TenantMember user);

    void resetPwd(TenantMember user);

    List<TenantMember> getMemberListByOrg(QueryVO<TenantMemberOrg> queryVO);

    void bindMemberOrg(BindVO bindVO);

    List<TenantMember> getMemberListByRoleOrg(TenantMemberOrgRole queryVO);

    void addMemberOrgRole(TenantMemberOrgRole bindVO);

    void delMemberOrgRole(TenantMemberOrgRole bindVO);
    /**
     * 获取用户授权信息
     */
    PowerVO getMemberPower(TenantMember user);

    // 邀请成员
    OperateUserInvite inviteMember(OperateUserInvite member);

    // 邀请成员成功
    void inviteMemberSuccess(OperateUser user);

    void updatePwd(Long userId, String password, String newPassword);

    NCLoginUserVO changeDefaultOption(NCLoginUserVO loginMember, TenantMember member);
}
