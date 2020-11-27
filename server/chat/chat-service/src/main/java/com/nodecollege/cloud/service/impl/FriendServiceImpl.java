package com.nodecollege.cloud.service.impl;

import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.ChatFriend;
import com.nodecollege.cloud.common.model.po.OperateUser;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.dao.mapper.ChatFriendMapper;
import com.nodecollege.cloud.feign.UserClient;
import com.nodecollege.cloud.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author LC
 * @date 2020/2/23 11:53
 */
@Service
public class FriendServiceImpl implements FriendService {

    @Autowired
    private ChatFriendMapper userFriendMapper;

    @Autowired
    private UserClient userClient;

    /**
     * 获取我的好友列表
     *
     * @param queryVO
     */
    @Override
    public List<ChatFriend> getFriendList(QueryVO<ChatFriend> queryVO) {
        NCUtils.nullOrEmptyThrow(queryVO.getData().getUserId(), new NCException("", "用户id必填！"));
        List<ChatFriend> friendList = userFriendMapper.selectFriendList(queryVO.toMap());
        for (int i = 0; i < friendList.size(); i++) {
            NCResult<OperateUser> userNCResult = userClient.getUserByUserId(friendList.get(i).getFriendId());
            if (!userNCResult.getSuccess() || userNCResult.getRows() == null || userNCResult.getRows().isEmpty()) {
                throw new NCException("", "无此用户！");
            }
            OperateUser user = userNCResult.getRows().get(0);
            friendList.get(i).setAvatar(user.getAvatar());
            friendList.get(i).setAvatarThumb(user.getAvatarThumb());
        }
        return friendList;
    }

    /**
     * 添加好友
     *
     * @param userFriend
     */
    @Override
    public void addFriend(ChatFriend userFriend) {
        NCUtils.nullOrEmptyThrow(userFriend.getUserId(), new NCException("", "用户id必填！"));
        NCUtils.nullOrEmptyThrow(userFriend.getFriendId(), new NCException("", "朋友id必填！"));
        NCUtils.nullOrEmptyThrow(userFriend.getSource(), new NCException("", "来源必填！"));
        NCResult<OperateUser> userNCResult = userClient.getUserByUserId(userFriend.getUserId());
        if (!userNCResult.getSuccess() || userNCResult.getRows() == null || userNCResult.getRows().isEmpty()) {
            throw new NCException("", "无此用户！");
        }
        OperateUser my = userNCResult.getRows().get(0);
        NCUtils.nullOrEmptyThrow(my, new NCException("", "用户不存在！"));
        userNCResult = userClient.getUserByUserId(userFriend.getFriendId());
        if (!userNCResult.getSuccess() || userNCResult.getRows() == null || userNCResult.getRows().isEmpty()) {
            throw new NCException("", "朋友不存在！");
        }
        OperateUser friend = userNCResult.getRows().get(0);

        ChatFriend query = new ChatFriend();
        query.setUserId(userFriend.getUserId());
        query.setFriendId(userFriend.getFriendId());
        List<ChatFriend> exList = userFriendMapper.selectFriendList(new QueryVO<>(query).toMap());
        if (exList.isEmpty()) {
            ChatFriend myR = new ChatFriend();
            myR.setUserId(userFriend.getUserId());
            myR.setFriendId(userFriend.getFriendId());
            myR.setFriendAccount(friend.getAccount());
            myR.setState(1);
            String friendNickName = NCUtils.isNotNullOrNotEmpty(userFriend.getNickname()) ? userFriend.getNickname() : friend.getNickname();
            friendNickName = NCUtils.isNotNullOrNotEmpty(friendNickName) ? friendNickName : friend.getAccount();
            myR.setNickname(friendNickName);
            myR.setSource(userFriend.getSource());
            myR.setCreateTime(new Date());

            ChatFriend friendR = new ChatFriend();
            friendR.setUserId(userFriend.getFriendId());
            friendR.setFriendId(userFriend.getUserId());
            friendR.setFriendAccount(my.getAccount());
            friendR.setState(0);
            friendR.setNickname(NCUtils.isNotNullOrNotEmpty(my.getNickname()) ? my.getNickname() : my.getAccount());
            friendR.setSource(userFriend.getSource());
            friendR.setCreateTime(new Date());

            userFriendMapper.insert(myR);
            userFriendMapper.insert(friendR);
            return;
        }

        query = exList.get(0);
        if (query.getState().equals(2)) {
            throw new NCException("", "已经是好友了！");
        }
        if (query.getState().equals(NCConstants.INT_NEGATIVE_1)) {
            throw new NCException("", "该好友被我加到黑名单了，请先从黑名单中移除！");
        }

        ChatFriend friendR = new ChatFriend();
        friendR.setUserId(userFriend.getFriendId());
        friendR.setFriendId(userFriend.getUserId());
        friendR = userFriendMapper.selectFriendList(new QueryVO<>(friendR).toMap()).get(0);
        if (friendR.getState().equals(NCConstants.INT_NEGATIVE_1)) {
            // 被好友拉黑了
            throw new NCException("", "已发送请求，等待朋友同意！");
        }
        // 朋友加我好友 我还没同意
        if (query.getState().equals(0)) {
            query.setState(2);
            if (!friendR.getState().equals(1)) {
                throw new NCException("", "数据错误！");
            }
            friendR.setState(2);

            userFriendMapper.updateByPrimaryKeySelective(query);
            userFriendMapper.updateByPrimaryKeySelective(friendR);
        }

        // 我之前发送过 朋友还没同意
        if (query.getState().equals(1)) {
            if (friendR.getState().equals(0)) {
                throw new NCException("", "已发送请求，等待朋友同意！");
            }
            if (friendR.getState().equals(3)) {
                // 拒绝后 可以再次发出请求
                friendR.setState(0);
                userFriendMapper.updateByPrimaryKeySelective(friendR);
            }
        }
        // 被我拒绝或删除了
        if (query.getState().equals(3)) {
            if (friendR.getSource().equals(2)) {
                query.setState(2);
                // 朋友还加我是好友
                userFriendMapper.updateByPrimaryKeySelective(query);
            }
            if (friendR.getState().equals(1)) {
                query.setState(2);
                friendR.setState(2);
                userFriendMapper.updateByPrimaryKeySelective(query);
                userFriendMapper.updateByPrimaryKeySelective(friendR);
            }
            if (friendR.getState().equals(3)) {
                // 拒绝后 可以再次发出请求
                friendR.setState(0);
                userFriendMapper.updateByPrimaryKeySelective(friendR);
                // 发出新申请
                query.setState(1);
                userFriendMapper.updateByPrimaryKeySelective(query);
            }
        }
    }

    /**
     * 处理好友请求
     *
     * @param userFriend
     */
    @Override
    public void handleRequest(ChatFriend userFriend) {
        NCUtils.nullOrEmptyThrow(userFriend.getUserFriendId(), new NCException("", "用户好友id必填！"));
        NCUtils.nullOrEmptyThrow(userFriend.getUserId(), new NCException("", "用户id必填！"));
        NCUtils.nullOrEmptyThrow(userFriend.getState(), new NCException("", "状态必填！"));

        ChatFriend ex = userFriendMapper.selectByPrimaryKey(userFriend.getUserFriendId());
        NCUtils.nullOrEmptyThrow(ex, new NCException("", "好友请求不存在！"));

        if (!ex.getState().equals(0)) {
            throw new NCException("", "数据错误！");
        }

        NCResult<OperateUser> userNCResult = userClient.getUserByUserId(userFriend.getUserId());
        if (!userNCResult.getSuccess() || userNCResult.getRows() == null || userNCResult.getRows().isEmpty()) {
            throw new NCException("", "无此用户！");
        }
        OperateUser my = userNCResult.getRows().get(0);
        NCUtils.nullOrEmptyThrow(my, new NCException("", "用户不存在！"));
        if (!ex.getUserId().equals(my.getUserId())) {
            throw new NCException("", "好友请求不存在！");
        }

        // 拒绝好友请求
        if (userFriend.getState().equals(3)) {
            userFriendMapper.updateByPrimaryKeySelective(userFriend);
        }

        // 同意好友请求
        if (userFriend.getState().equals(2)) {
            if (!ex.getState().equals(0)) {
                throw new NCException("", "只能处理朋友发给我的请求信息！");
            }
            userFriend.setState(2);

            ChatFriend friendR = new ChatFriend();
            friendR.setUserId(ex.getFriendId());
            friendR.setFriendId(userFriend.getUserId());
            friendR = userFriendMapper.selectFriendList(new QueryVO<>(friendR).toMap()).get(0);
            if (!friendR.getState().equals(1)) {
                throw new NCException("", "数据错误！");
            }

            friendR.setState(2);

            userFriendMapper.updateByPrimaryKeySelective(userFriend);
            userFriendMapper.updateByPrimaryKeySelective(friendR);
            return;
        }
        throw new NCException("", "处理状态错误！");
    }

    /**
     * 更新好友信息
     *
     * @param userFriend
     */
    @Override
    public void updateFriend(ChatFriend userFriend) {
        NCUtils.nullOrEmptyThrow(userFriend.getUserFriendId(), new NCException("", "用户好友id必填！"));
        NCUtils.nullOrEmptyThrow(userFriend.getUserId(), new NCException("", "用户id必填！"));

        ChatFriend ex = userFriendMapper.selectByPrimaryKey(userFriend.getUserFriendId());
        NCUtils.nullOrEmptyThrow(ex, new NCException("", "好友请求不存在！"));

        if (!ex.getState().equals(2)) {
            throw new NCException("", "数据错误！");
        }

        NCResult<OperateUser> userNCResult = userClient.getUserByUserId(userFriend.getUserId());
        if (!userNCResult.getSuccess() || userNCResult.getRows() == null || userNCResult.getRows().isEmpty()) {
            throw new NCException("", "用户不存在！");
        }
        OperateUser my = userNCResult.getRows().get(0);
        if (!ex.getUserId().equals(my.getUserId())) {
            throw new NCException("", "好友请求不存在！");
        }

        ChatFriend update = new ChatFriend();
        update.setUserFriendId(userFriend.getUserFriendId());
        update.setNickname(userFriend.getNickname());
        userFriendMapper.updateByPrimaryKeySelective(update);
    }

    /**
     * 删除好友
     *
     * @param userFriend
     */
    @Override
    public void delFriend(ChatFriend userFriend) {
        NCUtils.nullOrEmptyThrow(userFriend.getUserFriendId(), new NCException("", "用户好友id必填！"));
        NCUtils.nullOrEmptyThrow(userFriend.getUserId(), new NCException("", "用户id必填！"));

        ChatFriend ex = userFriendMapper.selectByPrimaryKey(userFriend.getUserFriendId());
        NCUtils.nullOrEmptyThrow(ex, new NCException("", "好友请求不存在！"));

        if (!ex.getState().equals(2)) {
            throw new NCException("", "数据错误！");
        }

        NCResult<OperateUser> userNCResult = userClient.getUserByUserId(userFriend.getUserId());
        if (!userNCResult.getSuccess() || userNCResult.getRows() == null || userNCResult.getRows().isEmpty()) {
            throw new NCException("", "用户不存在！");
        }
        OperateUser my = userNCResult.getRows().get(0);
        if (!ex.getUserId().equals(my.getUserId())) {
            throw new NCException("", "好友请求不存在！");
        }

        ChatFriend update = new ChatFriend();
        update.setUserFriendId(userFriend.getUserFriendId());
        update.setState(3);
        userFriendMapper.updateByPrimaryKeySelective(update);

    }

    /**
     * 添加到黑名单
     *
     * @param userFriend
     */
    @Override
    public void addBlack(ChatFriend userFriend) {
        NCUtils.nullOrEmptyThrow(userFriend.getUserFriendId(), new NCException("", "用户好友id必填！"));
        NCUtils.nullOrEmptyThrow(userFriend.getUserId(), new NCException("", "用户id必填！"));

        ChatFriend ex = userFriendMapper.selectByPrimaryKey(userFriend.getUserFriendId());
        NCUtils.nullOrEmptyThrow(ex, new NCException("", "好友请求不存在！"));

        NCResult<OperateUser> userNCResult = userClient.getUserByUserId(userFriend.getUserId());
        if (!userNCResult.getSuccess() || userNCResult.getRows() == null || userNCResult.getRows().isEmpty()) {
            throw new NCException("", "用户不存在！");
        }
        OperateUser my = userNCResult.getRows().get(0);
        if (!ex.getUserId().equals(my.getUserId())) {
            throw new NCException("", "好友请求不存在！");
        }

        ChatFriend update = new ChatFriend();
        update.setUserFriendId(userFriend.getUserFriendId());
        update.setState(NCConstants.INT_NEGATIVE_1);
        userFriendMapper.updateByPrimaryKeySelective(update);

        ChatFriend friend = new ChatFriend();
        friend.setUserId(ex.getFriendId());
        friend.setFriendId(ex.getUserId());
        friend = userFriendMapper.selectFriendList(new QueryVO<>(friend).toMap()).get(0);
        if (friend.getState().equals(2)) {
            friend.setState(1);
            userFriendMapper.updateByPrimaryKeySelective(friend);
        }
    }

    /**
     * 移出黑名单
     *
     * @param userFriend
     */
    @Override
    public void delBlack(ChatFriend userFriend) {
        NCUtils.nullOrEmptyThrow(userFriend.getUserFriendId(), new NCException("", "用户好友id必填！"));
        NCUtils.nullOrEmptyThrow(userFriend.getUserId(), new NCException("", "用户id必填！"));

        ChatFriend ex = userFriendMapper.selectByPrimaryKey(userFriend.getUserFriendId());
        NCUtils.nullOrEmptyThrow(ex, new NCException("", "好友请求不存在！"));

        NCResult<OperateUser> userNCResult = userClient.getUserByUserId(userFriend.getUserId());
        if (!userNCResult.getSuccess() || userNCResult.getRows() == null || userNCResult.getRows().isEmpty()) {
            throw new NCException("", "用户不存在！");
        }
        OperateUser my = userNCResult.getRows().get(0);
        if (!ex.getUserId().equals(my.getUserId())) {
            throw new NCException("", "好友请求不存在！");
        }

        if (!ex.getState().equals(NCConstants.INT_NEGATIVE_1)) {
            throw new NCException("", "好友不在黑名单中！");
        }

        ChatFriend friend = new ChatFriend();
        friend.setUserId(ex.getFriendId());
        friend.setFriendId(ex.getUserId());
        friend = userFriendMapper.selectFriendList(new QueryVO<>(friend).toMap()).get(0);


        ChatFriend update = new ChatFriend();
        update.setUserFriendId(userFriend.getUserFriendId());
        if (friend.getState().equals(1)) {
            friend.setState(2);
            userFriendMapper.updateByPrimaryKeySelective(friend);

            update.setState(2);
            userFriendMapper.updateByPrimaryKeySelective(update);
        }

        if (friend.getState().equals(0)) {
            update.setState(1);
            userFriendMapper.updateByPrimaryKeySelective(update);
        }

        if (friend.getState().equals(3)) {
            update.setState(1);
            userFriendMapper.updateByPrimaryKeySelective(update);
            friend.setState(0);
            userFriendMapper.updateByPrimaryKeySelective(friend);
        }
    }
}
