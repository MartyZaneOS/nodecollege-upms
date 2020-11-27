package com.nodecollege.cloud.service;

import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateUser;
import com.nodecollege.cloud.common.model.po.OperateUserInvite;

import java.util.List;

/**
 * @author LC
 * @date 2019/12/12 18:13
 */
public interface UserInviteService {

    /**
     * 获取邀请列表
     *
     * @param queryVO
     * @return
     */
    List<OperateUserInvite> getInvitedList(QueryVO<OperateUserInvite> queryVO);

    /**
     * 邀请成员
     *
     * @param invitation
     */
    OperateUser inviteMember(OperateUserInvite invitation);
}
