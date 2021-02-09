package com.nodecollege.cloud.service;

import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateSendMail;

import java.io.File;
import java.util.List;

/**
 * @author LC
 * @date 2021/1/21 15:09
 */
public interface SendMailService {

    // 查询邮件列表
    List<OperateSendMail> getList(QueryVO<OperateSendMail> queryVO);

    // 查询详情
    OperateSendMail getInfo(OperateSendMail sendMail);

    // 发送邮件
    OperateSendMail sendEmail(OperateSendMail sendMail);

    // 发送邮件验证码
    void sendEmailCert(String email, String cert);

    // 验证邮箱验证码
    Boolean checkEmailCert(String email, String cert);

    // 删除邮箱验证码
    void delEmailCert(String email);
}
