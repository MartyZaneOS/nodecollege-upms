package com.nodecollege.cloud.common.model.vo;

import lombok.Data;

/**
 * 活跃的聊天列表
 * 用户初次连接时返回
 *
 * @author LC
 * @date 2020/2/29 19:19
 */
@Data
public class ChatActiveData extends ChatData {
    /**
     * 当前用户id
     */
    private Long userId;
    /**
     * 活跃聊天室id
     */
    private String chatActiveId;
    /**
     * 聊天类
     * f-好友聊天
     * g-群组聊天
     */
    private String chatType;
    /**
     * 聊天室名称
     */
    private String chatName;
    /**
     * 群id
     * recordType为1-群组聊天时必填
     */
    private Long groupId;
    /**
     * 好友id
     * recordType为2-好友聊天时必填
     */
    private Long friendId;
    /**
     * 群组类型
     * 1-公司全员群
     * 2-公司部门群
     * 3-公司内部群
     * 4-公司外部群
     * 5-辩论群
     */
    private Integer groupType;
    /**
     * 接收状态
     * 消息类型为个人消息时必填
     * 1-未读
     * 2-已读
     */
    private Integer receiveState;

    /**
     * 新消息数量
     */
    private Integer newsNum = 0;

    /**
     * 头像信息
     */
    private String avatar;
    /**
     * 用户头像缩略图
     */
    private String avatarThumb;
}
