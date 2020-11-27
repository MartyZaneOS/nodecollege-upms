package com.nodecollege.cloud.common.model.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.utils.DateUtils;
import lombok.Data;

import java.util.Date;

/**
 * Table: chat_record
 * 版权：节点学院
 *
 * @author LC
 * @date 2020-02-16 01:02:23
 */
@Data
public class ChatRecord {
    /**
     * 主键
     */
    private Long recordId;
    /**
     * 发送用户id
     */
    private Long sendUserId;
    /**
     * 内容类型
     * 1-文字内容
     * 2-图片
     * 3-语音
     * 4-视频
     * 5-url
     * 6-表情
     * 7-进入聊天界面类型
     */
    private Integer contentType;
    /**
     * 发送消息内容
     * 进入聊天界面类型时为空
     */
    private String content;
    /**
     * 消息类型
     * 1-聊天群消息
     * 2-好友聊天消息
     */
    private Integer recordType;
    /**
     * 群组id
     * 消息类型未群组消息时必填
     */
    private Long groupId;
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
    /**
     * 发送时间
     */
    @JsonFormat(pattern = DateUtils.DATE_PATTERN.YYYY_MM_DD_HH_MM_SS, timezone = NCConstants.TIME_ZONE.SHANGHAI)
    private Date sendTime;
    /**
     * 接收时间
     * 消息类型为个人消息时有效
     */
    @JsonFormat(pattern = DateUtils.DATE_PATTERN.YYYY_MM_DD_HH_MM_SS, timezone = NCConstants.TIME_ZONE.SHANGHAI)
    private Date receiveTime;
    /**
     * 状态
     * 1-正常
     * 2-撤回
     */
    private Integer state;
}