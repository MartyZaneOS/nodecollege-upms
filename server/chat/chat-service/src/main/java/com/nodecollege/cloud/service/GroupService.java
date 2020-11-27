package com.nodecollege.cloud.service;

import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.ChatGroup;
import com.nodecollege.cloud.common.model.po.ChatGroupUser;
import com.nodecollege.cloud.common.model.vo.AddGroupVO;

import java.util.List;

/**
 * @author LC
 * @date 2020/2/22 18:34
 */
public interface GroupService {

    /**
     * 查询群组信息
     *
     * @param queryVO
     * @return
     */
    List<ChatGroupUser> getGroupList(QueryVO<ChatGroupUser> queryVO);

    /**
     * 添加群组
     *
     * @param addGroupVO
     */
    ChatGroup addGroup(AddGroupVO addGroupVO);

    /**
     * 添加租户群
     *
     * @param addGroupVO
     * @return
     */
    ChatGroup addTenantGroup(AddGroupVO addGroupVO);

    /**
     * 添加辩论群
     *
     * @param addGroupVO
     * @return
     */
    ChatGroup addDebateGroup(AddGroupVO addGroupVO);

    /**
     * 修改群组
     *
     * @param groupUser
     */
    ChatGroup updateGroup(ChatGroupUser groupUser);

    /**
     * 删除群组
     *
     * @param groupUser
     */
    void delGroup(ChatGroupUser groupUser);

    /**
     * 根据群id获取群信息
     * @param groupId
     * @return
     */
    ChatGroup getChatGroupById(Long groupId);

    /**
     * 清除群组缓存
     * @param groupId
     */
    void clearGroupCache(Long groupId);
}
