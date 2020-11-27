package com.nodecollege.cloud.common.model.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.utils.DateUtils;
import lombok.Data;

import java.util.Date;

/**
 * Table: chat_group_user
 * 版权：节点学院
 *
 * @author LC
 * @date 2020-02-16 00:43:36
 */
@Data
public class ChatGroupUser extends ChatGroup{
    /**
     * 群用户主键
     */
    private Long groupUserId;

    /**
     * 群组id
     */
    private Long groupId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户类型
     * 1-群主
     * 2-管理员
     * 3-普通成员
     */
    private Integer userType;

    /**
     * 成员id
     * 非公司外部群时必填
     */
    private Long memberId;

    /**
     * 状态
     * -1-被移除
     * 1-正常
     * 2-被禁言
     */
    private Integer state;

    /**
     * 1-正常
     * 2-屏蔽群消息
     */
    private Integer groupState;
    /**
     * 消息读取时间
     */
    @JsonFormat(pattern = DateUtils.DATE_PATTERN.YYYY_MM_DD_HH_MM_SS, timezone = NCConstants.TIME_ZONE.SHANGHAI)
    private Date recordReadTime;
    /**
     * 头像信息
     */
    private String avatar;
    /**
     * 用户头像缩略图
     */
    private String avatarThumb;
}