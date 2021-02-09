package com.nodecollege.cloud.common.model.po;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.utils.DateUtils;
import lombok.Data;

/**
 * Table: o_send_mail
 * 版权：节点学院
 *
 * @author LC
 * @date 2021-02-01 10:26:23
 */
@Data
public class OperateSendMail {
    /**
     * 发送邮件id
     */
    private Long id;

    /**
     * 邮件类型 0-用户注册邮箱验证码，1-管理员发送邮件
     */
    private Integer mailType;

    /**
     * 收件人，多个用英文格式逗号分隔
     */
    private String toMail;

    /**
     * 抄送人，多个用英文格式逗号分隔
     */
    private String replyTo;

    /**
     * 邮件主题
     */
    private String title;

    /**
     * 内容类型 0-文本内容，1-html内容
     */
    private Integer contentType;

    /**
     * 邮件附件 json格式存储文件名称和地址
     */
    private String files;

    /**
     * 发送状态 0-发送失败，1-发送成功
     */
    private Integer state;

    /**
     * 发送时间
     */
    @JsonFormat(pattern = DateUtils.DATE_PATTERN.YYYY_MM_DD_HH_MM_SS, timezone = NCConstants.TIME_ZONE.SHANGHAI)
    private Date createTime;

    /**
     * 邮件内容 存库
     */
    private byte[] content;

    // 邮件内容字符串 不存库
    private String contentStr;
}