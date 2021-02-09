package com.nodecollege.cloud.common.model.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.utils.DateUtils;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Table: o_feedback
 * 版权：节点学院
 *
 * @author LC
 * @date 2021-02-01 15:50:04
 */
@Data
public class OperateFeedback {
    /**
     * 主键
     */
    private Long id;

    /**
     * 意见反馈来源 0-首页，1-用户，2-租户
     */
    private Integer feedbackSource;

    // 是否是回复内容，0-不是，1-是
    private Integer isReply;

    /**
     * 回复id
     */
    private Long replyId;

    // 用户名称
    private String userName;

    /**
     * 用户邮箱，首页来源必填
     */
    private String userEmail;

    // 管理员id，管理员回复必填
    private Long adminId;
    /**
     * 用户id，用户来源必填
     */
    private Long userId;

    /**
     * 租户代码，租户来源必填
     */
    private String tenantCode;

    /**
     * 成员id，租户来源必填
     */
    private Long memberId;

    /**
     * 状态 0-未公开，1-公开，2-已删除
     */
    private Integer state;

    /**
     * 回复邮件id
     */
    private Long replyEmailId;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = DateUtils.DATE_PATTERN.YYYY_MM_DD_HH_MM_SS, timezone = NCConstants.TIME_ZONE.SHANGHAI)
    private Date createTime;

    /**
     * 意见反馈内容
     */
    private byte[] content;

    // 意见反馈内容字符串
    private String contentStr;

    // 回复列表
    private List<OperateFeedback> replyList;

    // 图片验证码
    private String imageCert;
}