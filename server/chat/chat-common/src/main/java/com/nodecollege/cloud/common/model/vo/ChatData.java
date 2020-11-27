package com.nodecollege.cloud.common.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.utils.DateUtils;
import lombok.Data;

import java.util.Date;

/**
 * @author LC
 * @date 2020/2/26 22:16
 */
@Data
public class ChatData {

    private String accessToken;

    private String infoRandom;

    /**
     * 用户id
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
     */
    private Integer contentType;

    /**
     * 发送消息内容
     */
    private String content;

    /**
     * 消息类型
     * 1-聊天群消息
     * 2-好友聊天消息
     * 3-第一次(或重连)初始化连接
     * 4-获取群组用户信息
     * 5-好友消息签收
     * 6-获取历史消息
     * 7-未读数量统计
     * 8-群用户信息修改
     * 9-辩论堂获取历史消息
     * 10-辩论堂消息
     * 11-记录点赞消息
     * 12-获取用户信息消息
     */
    private Integer recordType;

    /**
     * 发送时间
     */
    @JsonFormat(pattern = DateUtils.DATE_PATTERN.YYYY_MM_DD_HH_MM_SS, timezone = NCConstants.TIME_ZONE.SHANGHAI)
    private Date sendTime;

    /**
     * 接收时间
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
