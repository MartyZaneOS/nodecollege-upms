package com.nodecollege.cloud.common.model.po;

import lombok.Data;

import java.util.Date;

/**
 * Table: chat_debate_record
 * 版权：节点学院
 *
 * @author LC
 * @date 2020-04-19 11:58:31
 */
@Data
public class ChatDebateRecord {
    /**
     * 辩论记录id
     */
    private Long debateRecordId;

    /**
     * 辩论表id
     */
    private Long debateId;

    /**
     * 是否支持
     * 0-不支持
     * 1-支持
     */
    private Integer support;

    /**
     * 记录内容
     */
    private String record;

    /**
     * 反驳记录id
     */
    private Long refuteId;

    /**
     * 发表时间
     */
    private Date sendTime;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 支持数
     */
    private Integer supportNum;

    /**
     * 排序
     */
    private Integer sort;
    /**
     * 状态
     * 1-正常
     * 2-撤回
     */
    private Integer state;
}