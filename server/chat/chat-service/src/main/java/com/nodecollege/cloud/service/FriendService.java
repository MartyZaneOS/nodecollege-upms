package com.nodecollege.cloud.service;

import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.ChatFriend;

import java.util.List;

/**
 * @author LC
 * @date 2020/2/23 11:35
 */
public interface FriendService {

    /**
     * 查询我的好友列表
     *
     * @param queryVO
     */
    List<ChatFriend> getFriendList(QueryVO<ChatFriend> queryVO);

    /**
     * 添加好友
     *
     * @param userFriend
     */
    void addFriend(ChatFriend userFriend);

    /**
     * 处理添加请求
     */
    void handleRequest(ChatFriend userFriend);

    /**
     * 修改用户信息
     *
     * @param userFriend
     */
    void updateFriend(ChatFriend userFriend);

    /**
     * 删除好友
     *
     * @param userFriend
     */
    void delFriend(ChatFriend userFriend);

    /**
     * 添加到黑名单
     *
     * @param userFriend
     */
    void addBlack(ChatFriend userFriend);

    /**
     * 移出黑名单
     *
     * @param userFriend
     */
    void delBlack(ChatFriend userFriend);
}
