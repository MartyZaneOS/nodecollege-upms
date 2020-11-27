package com.nodecollege.cloud.common.model.vo;

import lombok.Data;

/**
 * @author LC
 * @date 2020/2/26 22:32
 */
@Data
public class ChatFriendData extends ChatData {

    /**
     * 接收用户id
     * 消息类型为个人消息时必填
     */
    private Long receiveUserId;

    /**
     * 接收状态
     * 消息类型为个人消息时必填
     * 1-未读
     * 2-已读
     */
    private Integer receiveState;
}
