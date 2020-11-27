package com.nodecollege.cloud.common.model.po;

import lombok.Data;

import java.util.Date;

/**
 * Table: chat_debate_relation
 * 版权：节点学院
 *
 * @author LC
 * @date 2020-04-19 13:59:28
 */
@Data
public class ChatDebateRelation {
    /**
     * 辩论堂节点关系id
     */
    private Long debateRelationId;

    /**
     * 辩论堂id
     */
    private Long debateId;

    /**
     * 节点id
     */
    private Long nodeId;

    /**
     * 辩论堂类型
     */
    private Integer debateType;

    /**
     * 是否主要节点
     * 1-是
     * 0-不是
     */
    private Integer master;

    /**
     * 辩论堂标题
     */
    private String title;

    /**
     * 是否完成
     * 0-结束
     * 1-辩论中
     */
    private Integer finish;

    /**
     * 创建时间
     */
    private Date createTime;
}