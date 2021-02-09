package com.nodecollege.cloud.service;

import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateFeedback;

import java.util.List;

/**
 * @author LC
 * @date 2021/2/1 15:53
 */
public interface FeedbackService {

    // 查询意见反馈列表
    List<OperateFeedback> getList(QueryVO<OperateFeedback> queryVO);

    // 添加意见反馈
    OperateFeedback addFeedback(OperateFeedback addFeedback);

    // 管理员回复意见反馈
    OperateFeedback replyFeedback(OperateFeedback reply);

    // 编辑意见反馈
    void editFeedback(OperateFeedback feedback);
}
