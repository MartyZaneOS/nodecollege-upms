package com.nodecollege.cloud.common.model.po;

import lombok.Data;

/**
 * Table: chat_debate_record_up
 * 版权：节点学院
 *
 * @author LC
 * @date 2020-04-19 12:09:13
 */
@Data
public class ChatDebateRecordUp {
    /**
     * 辩论记录点赞id
     */
    private Long debateRecordUpId;

    /**
     * 辩论表id
     */
    private Long debateId;

    /**
     * 辩论记录id
     */
    private Long debateRecordId;

    /**
     * 用户id
     */
    private Long userId;
}