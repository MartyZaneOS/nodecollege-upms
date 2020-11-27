package com.nodecollege.cloud.core.netty;

import com.alibaba.fastjson.JSONObject;
import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.enums.ErrorEnum;
import com.nodecollege.cloud.common.model.NCLoginUserVO;
import com.nodecollege.cloud.common.model.NCResponse;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.po.ChatGroupUser;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.service.GroupUserService;
import com.nodecollege.cloud.utils.ChatLoginUtils;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author LC
 * @date 2020/2/26 23:07
 */
@Slf4j
public abstract class ChatAbstrat {
    /**
     * 用户channel关联信息 一个用户可以有多个连接
     **/
    private static final Map<Long, HashSet<Channel>> USER_ID_CHANNEL_MAP = new ConcurrentHashMap();
    /**
     * 辩论堂channel关联信息
     */
    private static final Map<Long, HashSet<Channel>> DEBATE_ID_CHANNEL_MAP = new ConcurrentHashMap();

    @Autowired
    private GroupUserService groupUserService;

    @Autowired
    private ChatLoginUtils chatLoginUtils;

    /**
     * 用户连接时保存用户连接信息
     */
    public void connect(Channel channel) {
        NCLoginUserVO loginUser = chatLoginUtils.getUserLoginInfo();
        NCUtils.nullOrEmptyThrow(loginUser, ErrorEnum.LOGIN_TIME_OUT);
        // 从当前服务获取channel
        HashSet<Channel> channelList = USER_ID_CHANNEL_MAP.get(loginUser.getLoginId());
        if (channelList == null) {
            USER_ID_CHANNEL_MAP.put(loginUser.getLoginId(), new HashSet<>());
            channelList = USER_ID_CHANNEL_MAP.get(loginUser.getLoginId());
        }
        channelList.add(channel);
    }

    /**
     * 发送消息
     */
    public void sendMsg(Channel channel, String path, Object data) {
        NCResponse response = new NCResponse();
        response.setPath(path);
        response.setData(data);
        channel.writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(response)));
    }

    /**
     * 发送消息
     */
    public void sendMsg(Long userId, String path, NCResult result) {
        HashSet<Channel> channelList = USER_ID_CHANNEL_MAP.get(userId);
        if (channelList == null || channelList.isEmpty()) {
            return;
        }
        Iterator<Channel> it = channelList.iterator();
        while (it.hasNext()) {
            Channel channel = it.next();
            if (ChatHandler.GLOBAL_USERS.find(channel.id()) != null) {
                sendMsg(channel, path, result);
            } else {
                // 用户离线 删除channel
                it.remove();
            }
        }
        if (channelList.isEmpty()) {
            USER_ID_CHANNEL_MAP.remove(userId);
        }
    }

    /**
     * 发送群组消息
     */
    public void sendGroupMsg(Long groupId, String path, NCResult result) {
        List<ChatGroupUser> userList = groupUserService.getGroupUser(groupId);
        for (int i = 0; i < userList.size(); i++) {
            if (NCConstants.INT_NEGATIVE_1.equals(userList.get(i).getState())) {
                continue;
            }
            sendMsg(userList.get(i).getUserId(), path, result);
        }
    }

    /**
     * 辩论堂初始连接
     */
    public void debateConnect(Long debateId, Channel channel) {
        if (!DEBATE_ID_CHANNEL_MAP.containsKey(debateId)) {
            DEBATE_ID_CHANNEL_MAP.put(debateId, new HashSet<>());
        }
        DEBATE_ID_CHANNEL_MAP.get(debateId).add(channel);
    }

    /**
     * 向所有打开辩论堂的用户发送消息
     */
    public void sendDebateMsg(Long debateId, String path, NCResult result) {
        HashSet<Channel> channelHashSet = DEBATE_ID_CHANNEL_MAP.get(debateId);
        Iterator<Channel> it = channelHashSet.iterator();
        while (it.hasNext()) {
            Channel channel = it.next();
            if (ChatHandler.GLOBAL_USERS.find(channel.id()) != null) {
                sendMsg(channel, path, result);
            } else {
                // 用户离线 删除channel
                it.remove();
            }
        }
        if (channelHashSet.isEmpty()) {
            DEBATE_ID_CHANNEL_MAP.remove(debateId);
        }
    }
}
