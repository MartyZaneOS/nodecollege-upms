package com.nodecollege.cloud.service.impl;

import com.nodecollege.cloud.common.constants.RedisConstants;
import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateSendMail;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.common.utils.RedisUtils;
import com.nodecollege.cloud.dao.mapper.OperateSendMailMapper;
import com.nodecollege.cloud.service.SendMailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.List;

/**
 * @类名: MailSender<br>
 * @描述: 邮件发送<br>
 */
@Slf4j
@Service
public class SendMailServiceImpl implements SendMailService {

    @Autowired
    private OperateSendMailMapper sendMailMapper;

    @Autowired
    private JavaMailSender mailSender;

    //发件人名称设置
    @Value("${spring.mail.username}")
    private String sendUser;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public List<OperateSendMail> getList(QueryVO<OperateSendMail> queryVO) {
        return sendMailMapper.selectList(queryVO.toMap());
    }

    @Override
    public OperateSendMail getInfo(OperateSendMail sendMail) {
        NCUtils.nullOrEmptyThrow(sendMail.getId());
        sendMail = sendMailMapper.selectByPrimaryKey(sendMail.getId());
        if (sendMail == null) {
            throw new NCException("", "邮件不存在！");
        }
        sendMail.setContentStr(new String(sendMail.getContent()));
        sendMail.setContent(null);
        return sendMail;
    }

    @Override
    public OperateSendMail sendEmail(OperateSendMail sendMail) {
        NCUtils.nullOrEmptyThrow(sendMail.getTitle());
        NCUtils.nullOrEmptyThrow(sendMail.getContentType());
        NCUtils.nullOrEmptyThrow(sendMail.getContentStr());
        NCUtils.nullOrEmptyThrow(sendMail.getToMail());
        final MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(sendUser);
            helper.setSubject(sendMail.getTitle());
            if (sendMail.getContentType() == 1) {
                helper.setText(sendMail.getContentStr(), true);
            } else {
                helper.setText(sendMail.getContentStr());
            }
            //设置多个收件人
            String[] toAddress = sendMail.getToMail().split(",");
            helper.setTo(toAddress);
            //设置多个抄送
            if (StringUtils.isNotBlank(sendMail.getReplyTo())) {
                InternetAddress[] internetAddressCC = InternetAddress.parse(sendMail.getReplyTo());
                mimeMessage.setReplyTo(internetAddressCC);
            }
            //添加附件
            if (null != sendMail.getFiles()) {
                // todo 处理附件
//                for (File file : files) {
//                    helper.addAttachment(file.getName(), file);
//                }
            }
        } catch (MessagingException e) {
            log.error("发送邮件失败！格式问题。", e);
            throw new NCException("", "发送邮件失败！格式问题。");
        }
        mailSender.send(mimeMessage);
        sendMail.setState(1);
        sendMail.setCreateTime(new Date());
        sendMail.setContent(sendMail.getContentStr().getBytes());
        sendMailMapper.insertSelective(sendMail);
        return sendMail;
    }

    @Override
    public void sendEmailCert(String email, String cert) {
        Long ex = redisUtils.getExpire(RedisConstants.EMAIL_CERT + email);
        if (ex != null && ex > 60 * 4) {
            throw new NCException("", "已经发送邮件，请稍后再发！");
        }
        OperateSendMail sendMail = new OperateSendMail();
        sendMail.setTitle("验证码-节点学院");
        sendMail.setContentStr("您的验证码为 " + cert + " （有效期5分钟）");
        sendMail.setContentType(0);
        sendMail.setToMail(email);
        sendMail.setMailType(0);
        sendEmail(sendMail);
        redisUtils.set(RedisConstants.EMAIL_CERT + email, cert, 5 * 60);
    }

    @Override
    public Boolean checkEmailCert(String email, String cert) {
        String exCert = redisUtils.get(RedisConstants.EMAIL_CERT + email, String.class);
        if (null == cert || !cert.equalsIgnoreCase(exCert)) {
            return false;
        }
        return true;
    }

    @Override
    public void delEmailCert(String email) {
        redisUtils.delete(RedisConstants.EMAIL_CERT + email);
    }

}