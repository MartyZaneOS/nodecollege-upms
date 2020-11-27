package com.nodecollege.cloud.common.model.vo;

import lombok.Data;

import java.util.List;

/**
 * 签收消息数据
 *
 * @author LC
 * @date 2020/3/19 9:57
 */
@Data
public class ChatSignedData extends ChatData {

    /**
     * 好友消息签收列表
     */
    private List<Long> recordIdList;
    /**
     * 签收群组id
     */
    private Long groupId;
}
