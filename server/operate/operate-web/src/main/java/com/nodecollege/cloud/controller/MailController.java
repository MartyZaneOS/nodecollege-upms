package com.nodecollege.cloud.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nodecollege.cloud.common.constants.HeaderConstants;
import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateSendMail;
import com.nodecollege.cloud.common.model.vo.SendMailVO;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.service.CommonService;
import com.nodecollege.cloud.service.SendMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author LC
 * @date 2021/1/21 15:15
 */
@RestController
@RequestMapping("/mail")
public class MailController {

    @Autowired
    private SendMailService sendMailService;

    @Autowired
    private CommonService commonService;

    @PostMapping("/getList")
    public NCResult<OperateSendMail> getList(@RequestBody QueryVO<OperateSendMail> queryVO) {
        Page page = PageHelper.startPage(queryVO.getPageNum(), queryVO.getPageSize());
        List<OperateSendMail> list = sendMailService.getList(queryVO);
        return NCResult.ok(list, page.getTotal());
    }

    @PostMapping("/getInfo")
    public NCResult<OperateSendMail> getInfo(@RequestBody OperateSendMail sendMail) {
        OperateSendMail info = sendMailService.getInfo(sendMail);
        return NCResult.ok(info);
    }

    @PostMapping("/sendMail")
    public NCResult sendMail(@RequestBody SendMailVO sendMailVO, HttpServletRequest request) {
        String imageCertSessionId = NCUtils.getCookieValue(HeaderConstants.IMAGE_CERT_SESSION_ID, request);
        if (!commonService.checkImageCert(imageCertSessionId, sendMailVO.getImageCert())) {
            throw new NCException("-1", "图片验证码错误，请刷新后重试！");
        }
        OperateSendMail sendMail = new OperateSendMail();
        sendMail.setMailType(1);
        sendMail.setTitle(sendMailVO.getTitle());
        sendMail.setContentType(sendMailVO.getContentType());
        sendMail.setContentStr(sendMailVO.getContent());
        sendMail.setToMail(sendMailVO.getToMail());
        sendMailService.sendEmail(sendMail);
        return NCResult.ok();
    }
}
