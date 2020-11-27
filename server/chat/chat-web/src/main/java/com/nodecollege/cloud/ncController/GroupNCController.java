package com.nodecollege.cloud.ncController;

import com.nodecollege.cloud.common.annotation.NCController;
import com.nodecollege.cloud.common.annotation.NCRequestMapping;
import com.nodecollege.cloud.common.enums.ErrorEnum;
import com.nodecollege.cloud.common.model.NCLoginUserVO;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.po.ChatGroup;
import com.nodecollege.cloud.common.model.po.ChatGroupUser;
import com.nodecollege.cloud.common.model.vo.AddGroupVO;
import com.nodecollege.cloud.common.model.vo.ChatGroupData;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.core.netty.ChatAbstrat;
import com.nodecollege.cloud.service.GroupService;
import com.nodecollege.cloud.service.GroupUserService;
import com.nodecollege.cloud.utils.ChatLoginUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 获取群组用户信息
 *
 * @author LC
 * @date 2020/2/26 23:07
 */
@Slf4j
@NCController
@NCRequestMapping(value = "/chat")
public class GroupNCController extends ChatAbstrat {

    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupUserService groupUserService;

    @Autowired
    private ChatLoginUtils chatLoginUtils;

    /**
     * 更新群
     *
     * @param groupUser
     */
    @NCRequestMapping(value = "/updateGroup")
    public void updateGroup(ChatGroupUser groupUser) {
        log.info("ChatUpdateGroupServiceImpl handler");
        NCLoginUserVO loginUser = chatLoginUtils.getUserLoginInfo();
        NCUtils.nullOrEmptyThrow(loginUser, ErrorEnum.LOGIN_TIME_OUT);
        groupUser.setUserId(loginUser.getLoginId());
        ChatGroup group = groupService.updateGroup(groupUser);
        // 给群组发送
        sendGroupMsg(group.getGroupId(), "/chat/updateGroup", NCResult.ok(group));
    }

    /**
     * 获取群组用户信息
     *
     * @param groupData
     */
    @NCRequestMapping(value = "/getGroupUser")
    public NCResult getGroupUser(ChatGroupData groupData) {
        return NCResult.ok(groupUserService.getGroupUser(groupData.getGroupId()));
    }

    /**
     * 添加群用户
     *
     * @param groupData
     */
    @NCRequestMapping(value = "/addGroupUser")
    public void addGroupUser(AddGroupVO groupData) {
        log.info("ChatAddGroupUserServiceImpl handler");
        NCLoginUserVO loginUser = chatLoginUtils.getUserLoginInfo();
        NCUtils.nullOrEmptyThrow(loginUser, ErrorEnum.LOGIN_TIME_OUT);
        Long userId = loginUser.getLoginId();
        AddGroupVO groupUser = new AddGroupVO();
        groupUser.setUserId(userId);
        groupUser.setGroupId(groupData.getGroupId());
        groupUser.setUserList(groupData.getUserList());
        List<ChatGroupUser> userList = groupUserService.addGroupUser(groupUser);
        // 给群所有人发送发送
        sendGroupMsg(groupData.getGroupId(), "/chat/updateGroupUser", NCResult.ok(userList));
    }

    /**
     * 删除群组用户信息
     *
     * @param groupData
     */
    @NCRequestMapping(value = "/delGroupUser")
    public void delGroupUser(AddGroupVO groupData) {
        log.info("ChatAddGroupUserServiceImpl handler");
        NCLoginUserVO loginUser = chatLoginUtils.getUserLoginInfo();
        NCUtils.nullOrEmptyThrow(loginUser, ErrorEnum.LOGIN_TIME_OUT);
        Long userId = loginUser.getLoginId();
        AddGroupVO groupUser = new AddGroupVO();
        groupUser.setUserId(userId);
        groupUser.setGroupId(groupData.getGroupId());
        groupUser.setUserList(groupData.getUserList());
        List<ChatGroupUser> userList = groupUserService.delGroupUser(groupUser);
        // 给群所有人发送发送
        sendGroupMsg(groupData.getGroupId(), "/chat/updateGroupUser", NCResult.ok(userList));
    }

    /**
     * 更新群组用户信息
     *
     * @param groupUser
     */
    @NCRequestMapping(value = "/updateGroupUser")
    public void updateGroupUser(ChatGroupUser groupUser) {
        log.info("ChatUpdateGroupUserServiceImpl handler");
        NCLoginUserVO loginUser = chatLoginUtils.getUserLoginInfo();
        NCUtils.nullOrEmptyThrow(loginUser, ErrorEnum.LOGIN_TIME_OUT);
        groupUser.setUserId(loginUser.getLoginId());
        groupUser = groupUserService.updateGroupUser(groupUser);
        // 给群所有人发送
        sendGroupMsg(groupUser.getGroupId(), "/chat/updateGroupUser", NCResult.ok(groupUser));
    }
}
