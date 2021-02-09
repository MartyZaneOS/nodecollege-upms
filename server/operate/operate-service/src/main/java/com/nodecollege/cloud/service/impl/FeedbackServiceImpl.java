package com.nodecollege.cloud.service.impl;

import com.nodecollege.cloud.client.utils.NCLoginUtils;
import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.NCLoginUserVO;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateFeedback;
import com.nodecollege.cloud.common.model.po.OperateSendMail;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.dao.mapper.OperateFeedbackMapper;
import com.nodecollege.cloud.service.FeedbackService;
import com.nodecollege.cloud.service.SendMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author LC
 * @date 2021/2/1 15:53
 */
@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private OperateFeedbackMapper feedbackMapper;

    @Autowired
    private NCLoginUtils loginUtils;

    @Autowired
    private SendMailService sendMailService;

    @Override
    public List<OperateFeedback> getList(QueryVO<OperateFeedback> queryVO) {
        NCUtils.nullOrEmptyThrow(queryVO.getData());
        queryVO.getData().setIsReply(0);
        List<OperateFeedback> list = feedbackMapper.selectList(queryVO.toMap());

        queryVO.getData().setIsReply(1);
        queryVO.setLongList(new ArrayList<>());
        list.forEach(item -> queryVO.getLongList().add(item.getId()));

        List<OperateFeedback> replyList = feedbackMapper.selectList(queryVO.toMap());
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setContentStr(new String(list.get(i).getContent()));
            Iterator<OperateFeedback> replyIterator = replyList.iterator();
            while (replyIterator.hasNext()) {
                OperateFeedback item = replyIterator.next();
                if (item.getReplyId().equals(list.get(i).getId())) {
                    item.setContentStr(new String(item.getContent()));
                    if (list.get(i).getReplyList() == null) list.get(i).setReplyList(new ArrayList<>());
                    list.get(i).getReplyList().add(item);
                    replyIterator.remove();
                }
            }
        }
        for (int i = 0; i < list.size();i++) {
            if (list.get(i).getReplyList() != null) {
                list.get(i).getReplyList().sort(Comparator.comparing(OperateFeedback::getCreateTime));
            }
        }
        return list;
    }

    @Override
    public OperateFeedback addFeedback(OperateFeedback addFeedback) {
        NCUtils.nullOrEmptyThrow(addFeedback.getIsReply());
        if (addFeedback.getIsReply() == 1) {
            NCUtils.nullOrEmptyThrow(addFeedback.getReplyId());
            OperateFeedback reply = feedbackMapper.selectByPrimaryKey(addFeedback.getReplyId());
            if (reply == null || reply.getState() != 1) {
                throw new NCException("", "回复记录不存在！");
            }
            if (reply.getFeedbackSource() == 2 && !reply.getTenantCode().equals(addFeedback.getTenantCode())) {
                throw new NCException("", "无权回复该记录！");
            }
            if (reply.getFeedbackSource() == 1 && !reply.getUserId().equals(addFeedback.getUserId())) {
                throw new NCException("", "无权回复该记录！");
            }
        }
        addFeedback.setAdminId(null);
        addFeedback.setState(1);
        addFeedback.setCreateTime(new Date());
        addFeedback.setContent(addFeedback.getContentStr().getBytes());
        feedbackMapper.insertSelective(addFeedback);
        return addFeedback;
    }

    @Override
    public OperateFeedback replyFeedback(OperateFeedback reply) {
        NCUtils.nullOrEmptyThrow(reply.getReplyId());
        NCUtils.nullOrEmptyThrow(reply.getAdminId());
        NCUtils.nullOrEmptyThrow(reply.getUserName());
        OperateFeedback ex = feedbackMapper.selectByPrimaryKey(reply.getReplyId());
        NCUtils.nullOrEmptyThrow(ex, "", "该记录不存在！");

        if (reply.getReplyEmailId() != null && reply.getReplyEmailId() == 99999) {
            OperateSendMail sendMail = new OperateSendMail();
            sendMail.setTitle("意见反馈管理员回复");
            sendMail.setToMail(reply.getUserEmail());
            sendMail.setContentType(0);
            sendMail.setContentStr(reply.getContentStr());
            sendMail = sendMailService.sendEmail(sendMail);
            reply.setReplyEmailId(sendMail.getId());
        }

        reply.setFeedbackSource(ex.getFeedbackSource());
        reply.setIsReply(1);
        reply.setContent(reply.getContentStr().getBytes());
        reply.setUserId(ex.getUserId());
        reply.setTenantCode(ex.getTenantCode());
        reply.setMemberId(ex.getMemberId());
        reply.setState(1);
        reply.setCreateTime(new Date());
        feedbackMapper.insertSelective(reply);
        return reply;
    }

    @Override
    public void editFeedback(OperateFeedback feedback) {
        NCUtils.nullOrEmptyThrow(feedback.getId());
        OperateFeedback ex = feedbackMapper.selectByPrimaryKey(feedback.getId());
        if (null == ex || ex.getState() == 2) throw new NCException("", "意见反馈不存在！");
        if (feedback.getState() == 2) {
            if (ex.getFeedbackSource() == 0) {
                throw new NCException("", "首页意见反馈不能删除！");
            } else if (ex.getFeedbackSource() == 1) {
                NCLoginUserVO userLogin = loginUtils.getUserLoginInfo();
                if (userLogin == null) {
                    throw new NCException("", "用户未登录！");
                }
                if (!userLogin.getLoginId().equals(ex.getUserId())) {
                    throw new NCException("", "无权删除！");
                }
            } else {
                NCLoginUserVO memberLogin = loginUtils.getMemberLoginInfo();
                if (memberLogin == null) {
                    throw new NCException("", "成员未登录！");
                }
                if (!memberLogin.getTenantCode().equals(ex.getTenantCode()) || memberLogin.getLoginId().equals(ex.getMemberId())) {
                    throw new NCException("", "无权删除！");
                }
            }
        } else {
            NCLoginUserVO adminLogin = loginUtils.getAdminLoginInfo();
            if (adminLogin == null) throw new NCException("", "管理员未登录！");
        }
        OperateFeedback update = new OperateFeedback();
        update.setId(feedback.getId());
        update.setState(feedback.getState());
        feedbackMapper.updateByPrimaryKeySelective(update);
    }
}
