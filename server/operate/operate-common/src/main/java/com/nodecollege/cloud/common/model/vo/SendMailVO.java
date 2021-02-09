package com.nodecollege.cloud.common.model.vo;

import lombok.Data;

import java.io.File;
import java.util.List;

/**
 * @author LC
 * @date 2021/1/21 15:17
 */
@Data
public class SendMailVO {
    // 邮件类型 0-用户注册邮箱验证码，1-管理员发送邮件，2-意见反馈回复邮件
    private Integer mailType;
    // 收件人，多个用英文格式逗号分隔
    private String toMail;
    // 抄送人，多个用英文格式逗号分隔
    private String replyTo;
    // 邮件主题
    private String title;
    // 内容类型 0-文本内容，1-html内容
    private Integer contentType;
    // 邮件内容
    private String content;
    // 图片验证码
    private String imageCert;
    // 附件
    private List<File> files;
}
