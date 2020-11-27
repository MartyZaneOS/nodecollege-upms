package com.nodecollege.cloud.ncController;

import com.nodecollege.cloud.common.annotation.NCController;
import com.nodecollege.cloud.common.annotation.NCRequestMapping;
import com.nodecollege.cloud.common.enums.ErrorEnum;
import com.nodecollege.cloud.common.model.NCLoginUserVO;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.ChatDebate;
import com.nodecollege.cloud.common.model.po.ChatDebateRecord;
import com.nodecollege.cloud.common.model.po.ChatDebateRecordUp;
import com.nodecollege.cloud.common.model.po.OperateUser;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.core.netty.ChatAbstrat;
import com.nodecollege.cloud.service.DebateService;
import com.nodecollege.cloud.utils.ChatLoginUtils;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 辩论堂
 *
 * @author LC
 * @date 2020/5/4 20:31
 */
@NCController
@NCRequestMapping(value = "/debate")
public class DebateNCController extends ChatAbstrat {

    @Autowired
    private DebateService debateService;

    @Autowired
    private ChatLoginUtils chatLoginUtils;

    /**
     * 辩论堂初次链接
     */
    @NCRequestMapping(value = "/debateConnect")
    public NCResult<ChatDebate> debateConnect(ChatDebate debate, Channel channel) {
        debateConnect(debate.getDebateId(), channel);
        return NCResult.ok();
    }

    /**
     * 初始化辩论堂消息
     */
    @NCRequestMapping(value = "/getRecordList")
    public NCResult<ChatDebateRecord> getRecordList(QueryVO<ChatDebateRecord> queryVO) {
        return debateService.getRecordList(queryVO);
    }

    /**
     * 辩论堂发送消息
     */
    @NCRequestMapping(value = "/sendRecord")
    public void sendRecord(ChatDebateRecord record) {
        NCLoginUserVO loginUser = chatLoginUtils.getUserLoginInfo();
        NCUtils.nullOrEmptyThrow(loginUser, ErrorEnum.LOGIN_TIME_OUT);
        record.setUserId(loginUser.getLoginId());
        record = debateService.addRecord(record);
        // 给所有打开这个辩论堂的人发送
        sendDebateMsg(record.getDebateId(), "/debate/getRecordList", NCResult.ok(record));
        sendDebateMsg(record.getDebateId(), "/debate/updateDebateSupport", NCResult.ok(debateService.getDebateCache(record.getDebateId())));
    }

    /**
     * 获取用户信息
     */
    @NCRequestMapping(value = "/getUserInfo")
    public NCResult<OperateUser> getUserInfo(QueryVO queryVO) {
        List<OperateUser> list = debateService.getUserInfo(queryVO);
        return NCResult.ok(list);
    }

    /**
     * 辩论堂消息点赞
     */
    @NCRequestMapping(value = "/recordUp")
    public void recordUp(ChatDebateRecordUp recordUp) {
        NCLoginUserVO loginUser = chatLoginUtils.getUserLoginInfo();
        NCUtils.nullOrEmptyThrow(loginUser, ErrorEnum.LOGIN_TIME_OUT);
        recordUp.setUserId(loginUser.getLoginId());
        recordUp = debateService.addRecordUp(recordUp);
        if (recordUp == null) {
            return;
        }
        // 给所有打开这个辩论堂的人发送
        sendDebateMsg(recordUp.getDebateId(), "/debate/recordUp", NCResult.ok(recordUp));
        sendDebateMsg(recordUp.getDebateId(), "/debate/updateDebateSupport", NCResult.ok(debateService.getDebateCache(recordUp.getDebateId())));
    }

    /**
     * 辩论堂取消消息点赞
     */
    @NCRequestMapping(value = "/delRecordUp")
    public void delRecordUp(ChatDebateRecordUp recordUp) {
        NCLoginUserVO loginUser = chatLoginUtils.getUserLoginInfo();
        NCUtils.nullOrEmptyThrow(loginUser, ErrorEnum.LOGIN_TIME_OUT);
        recordUp.setUserId(loginUser.getLoginId());
        recordUp = debateService.delRecordUp(recordUp);
        if (recordUp == null) {
            return;
        }
        // 给所有打开这个辩论堂的人发送
        sendDebateMsg(recordUp.getDebateId(), "/debate/delRecordUp", NCResult.ok(recordUp));
        sendDebateMsg(recordUp.getDebateId(), "/debate/updateDebateSupport", NCResult.ok(debateService.getDebateCache(recordUp.getDebateId())));
    }

    /**
     * 辩论堂取消消息点赞
     */
    @NCRequestMapping(value = "/getUpUserList")
    public NCResult<OperateUser> getUpUserList(ChatDebateRecordUp recordUp) {
        List<OperateUser> userList = debateService.getUpUserList(recordUp);
        return NCResult.ok(userList);
    }

    /**
     * 辩论堂取消消息点赞
     */
    @NCRequestMapping(value = "/getMyUpList")
    public NCResult<ChatDebateRecordUp> getMyUpList(ChatDebateRecordUp recordUp) {
        NCLoginUserVO loginUser = chatLoginUtils.getUserLoginInfo();
        NCUtils.nullOrEmptyThrow(loginUser, ErrorEnum.LOGIN_TIME_OUT);
        recordUp.setUserId(loginUser.getLoginId());
        List<ChatDebateRecordUp> recordList = debateService.getMyUpList(recordUp);
        return NCResult.ok(recordList);
    }
}
