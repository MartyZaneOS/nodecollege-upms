package com.nodecollege.cloud.service;

import com.nodecollege.cloud.common.model.po.ChatGroupUser;
import com.nodecollege.cloud.common.model.vo.AddGroupVO;

import java.util.List;

/**
 * @author LC
 * @date 2020/3/10 20:43
 */
public interface GroupUserService {

    /**
     * 根据群组id获取群用户信息
     *
     * @param groupId
     * @return
     */
    List<ChatGroupUser> getGroupUser(Long groupId);

    /**
     * 更新群组用户记录阅读时间
     *
     * @param groupUser
     */
    void updateRecordReadTime(ChatGroupUser groupUser);

    /**
     * 更新群组用户信息
     * 昵称或者群消息设置
     *
     * @param groupUser
     * @return
     */
    ChatGroupUser updateGroupUser(ChatGroupUser groupUser);

    /**
     * 添加群组用户信息
     *
     * @param groupVO
     * @return
     */
    List<ChatGroupUser> addGroupUser(AddGroupVO groupVO);

    /**
     * 退出群聊
     *
     * @param chatGroupUser
     * @return
     */
    List<ChatGroupUser> delGroupUser(AddGroupVO chatGroupUser);
}
