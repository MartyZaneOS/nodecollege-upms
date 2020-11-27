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
public class ChatRecordData extends ChatData {
    /**
     * 分页
     */
    private Integer pageNum = 1;
    /**
     * 分页大小
     */
    private Integer pageSize = 20;
    /**
     * 群id
     */
    private Long groupId;
    /**
     * 接收用户id
     */
    private Long receiveUserId;
}
