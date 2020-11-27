package com.nodecollege.cloud.common.model.vo;

import lombok.Data;

/**
 * @author LC
 * @date 2020/2/26 22:30
 */
@Data
public class ChatGroupData extends ChatData {

    /**
     * 群组id
     * 消息类型未群组消息时必填
     */
    private Long groupId;
    /**
     * 群名称
     */
    private String groupName;
}
