package com.nodecollege.cloud.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nodecollege.cloud.client.utils.NCLoginUtils;
import com.nodecollege.cloud.common.constants.HeaderConstants;
import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.NCLoginUserVO;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateFeedback;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.service.CommonService;
import com.nodecollege.cloud.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author LC
 * @date 2021/2/1 16:00
 */
@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private NCLoginUtils loginUtils;

    @Autowired
    private CommonService commonService;

    // 运维运营使用
    @PostMapping("/getList")
    public NCResult<OperateFeedback> getList(@RequestBody QueryVO<OperateFeedback> queryVO) {
        Page page = PageHelper.startPage(queryVO.getPageNum(), queryVO.getPageSize());
        List<OperateFeedback> list = feedbackService.getList(queryVO);
        return NCResult.ok(list, page.getTotal());
    }

    // 首页使用
    @PostMapping("/getIndexList")
    public NCResult<OperateFeedback> getIndexList(@RequestBody QueryVO<OperateFeedback> queryVO) {
        Page page = PageHelper.startPage(queryVO.getPageNum(), queryVO.getPageSize());
        List<OperateFeedback> list = feedbackService.getList(queryVO);
        for (OperateFeedback feedback : list) {
            if (feedback.getState() == 0) feedback.setContentStr(null);
            if (feedback.getState() == 2) feedback.setContentStr(null);
            if (feedback.getReplyList() != null) {
                for (OperateFeedback reply : feedback.getReplyList()) {
                    if (reply.getState() == 0) reply.setContentStr(null);
                    if (reply.getState() == 2) reply.setContentStr(null);
                }
            }
        }
        return NCResult.ok(list, page.getTotal());
    }

    // 添加意见反馈
    @PostMapping("/addFeedback")
    public NCResult addFeedback(@RequestBody OperateFeedback addFeedback, HttpServletRequest request) {
        String imageCertSessionId = NCUtils.getCookieValue(HeaderConstants.IMAGE_CERT_SESSION_ID, request);
        if (!commonService.checkImageCert(imageCertSessionId, addFeedback.getImageCert())) {
            throw new NCException("-1", "图片验证码错误，请刷新后重试！");
        }
        NCUtils.nullOrEmptyThrow(addFeedback.getFeedbackSource());
        if (0 == addFeedback.getFeedbackSource()) {
            NCUtils.nullOrEmptyThrow(addFeedback.getUserName());
            if (addFeedback.getUserName().length() > 10) {
                throw new NCException("", "用户名最大10个字符！");
            }
        } else if (1 == addFeedback.getFeedbackSource()) {
            NCLoginUserVO userLogin = loginUtils.getUserLoginInfo();
            if (userLogin == null) throw new NCException("", "用户未登录！");
            addFeedback.setUserId(userLogin.getLoginId());
            addFeedback.setUserName(userLogin.getAccount());
        } else {
            NCLoginUserVO memberLogin = loginUtils.getMemberLoginInfo();
            if (memberLogin == null) throw new NCException("", "成员未登陆！");
            addFeedback.setTenantCode(memberLogin.getTenantCode());
            addFeedback.setMemberId(memberLogin.getLoginId());
            addFeedback.setUserName(memberLogin.getAccount());
        }
        OperateFeedback add = feedbackService.addFeedback(addFeedback);
        return NCResult.ok(add);
    }

    // 管理员回复意见反馈
    @PostMapping("/replyFeedback")
    public NCResult replyFeedback(@RequestBody OperateFeedback reply, HttpServletRequest request) {
        String imageCertSessionId = NCUtils.getCookieValue(HeaderConstants.IMAGE_CERT_SESSION_ID, request);
        if (!commonService.checkImageCert(imageCertSessionId, reply.getImageCert())) {
            throw new NCException("-1", "图片验证码错误，请刷新后重试！");
        }
        NCLoginUserVO adminLoginInfo = loginUtils.getAdminLoginInfo();
        reply.setAdminId(adminLoginInfo.getLoginId());
        reply.setUserName(adminLoginInfo.getAccount());
        OperateFeedback add = feedbackService.replyFeedback(reply);
        return NCResult.ok(add);
    }

    // 编辑意见反馈
    @PostMapping("editFeedback")
    public NCResult editFeedback(@RequestBody OperateFeedback feedback) {
        feedbackService.editFeedback(feedback);
        return NCResult.ok();
    }
}
