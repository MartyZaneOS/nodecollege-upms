package com.nodecollege.cloud.ncController;

import com.nodecollege.cloud.common.annotation.NCController;
import com.nodecollege.cloud.common.annotation.NCRequestMapping;
import com.nodecollege.cloud.common.enums.ErrorEnum;
import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.NCLoginUserVO;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.ChatGroupUser;
import com.nodecollege.cloud.common.model.po.ChatRecord;
import com.nodecollege.cloud.common.model.vo.ChatRecordData;
import com.nodecollege.cloud.common.model.vo.ChatSignedData;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.core.netty.ChatAbstrat;
import com.nodecollege.cloud.service.GroupUserService;
import com.nodecollege.cloud.service.RecordService;
import com.nodecollege.cloud.utils.ChatLoginUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 好友聊天消息
 *
 * @author LC
 * @date 2020/2/26 23:07
 */
@Slf4j
@NCController
@NCRequestMapping(value = "/chat")
public class RecordNCController extends ChatAbstrat {

    @Autowired
    private RecordService recordService;

    @Autowired
    private GroupUserService groupUserService;
    @Autowired
    private ChatLoginUtils chatLoginUtils;

    /**
     * 消息处理
     */
    @NCRequestMapping(value = "/getRecord")
    public NCResult<ChatRecord> getRecord(ChatRecordData recordData) {
        NCLoginUserVO loginUser = chatLoginUtils.getUserLoginInfo();
        NCUtils.nullOrEmptyThrow(loginUser, ErrorEnum.LOGIN_TIME_OUT);
        QueryVO<ChatRecord> queryVO = new QueryVO<>(new ChatRecord());
        queryVO.setUserId(loginUser.getLoginId());
        queryVO.setPageSize(recordData.getPageSize());
        queryVO.setPageNum(recordData.getPageNum());
        queryVO.getData().setReceiveUserId(recordData.getReceiveUserId());
        queryVO.getData().setSendTime(recordData.getSendTime());
        if (recordData.getGroupId() != null) {
            queryVO.getData().setGroupId(recordData.getGroupId());
        } else {
            queryVO.getData().setSendUserId(loginUser.getLoginId());
        }
        return recordService.getRecord(queryVO);
    }

    /**
     * 添加好友消息记录
     *
     * @param record
     */
    @NCRequestMapping(value = "/addRecord")
    public NCResult addRecord(ChatRecord record) {
        NCLoginUserVO loginUser = chatLoginUtils.getUserLoginInfo();
        NCUtils.nullOrEmptyThrow(loginUser, ErrorEnum.LOGIN_TIME_OUT);
        record.setSendUserId(loginUser.getLoginId());
        record = recordService.addRecord(record);
        // 给朋友发送
        NCResult result = NCResult.ok(record);
        sendMsg(record.getReceiveUserId(), "/chat/addRecord", result);
        return result;
    }

    /**
     * 添加群组消息
     *
     * @param record
     */
    @NCRequestMapping(value = "/addGroupRecord")
    public void handler(ChatRecord record) {
        log.info("ChatHandlerGroupServiceImpl handler");
        NCLoginUserVO loginUser = chatLoginUtils.getUserLoginInfo();
        NCUtils.nullOrEmptyThrow(loginUser, ErrorEnum.LOGIN_TIME_OUT);
        record.setSendUserId(loginUser.getLoginId());
        record = recordService.addRecord(record);
        // 给群组发送
        sendGroupMsg(record.getGroupId(), "/chat/addGroupRecord", NCResult.ok(record));
    }

    /**
     * 消息签收
     *
     * @param signedData
     */
    @NCRequestMapping(value = "/signed")
    public void handler(ChatSignedData signedData) {
        log.info("ChatHandlerSignedServiceImpl handler");
        if (signedData == null) {
            return;
        }
        NCLoginUserVO loginUser = chatLoginUtils.getUserLoginInfo();
        NCUtils.nullOrEmptyThrow(loginUser, ErrorEnum.LOGIN_TIME_OUT);
        Long userId = loginUser.getLoginId();
        if (signedData.getGroupId() != null) {
            // 群消息签收
            ChatGroupUser update = new ChatGroupUser();
            update.setGroupId(signedData.getGroupId());
            update.setUserId(userId);
            groupUserService.updateRecordReadTime(update);
            sendMsg(userId, "/chat/signed", NCResult.ok(update));
            return;
        } else if (NCUtils.isNotNullOrNotEmpty(signedData.getRecordIdList()) && signedData.getSendUserId() != null) {
            // 好友消息签收
            for (int i = 0; i < signedData.getRecordIdList().size(); i++) {
                ChatRecord record = new ChatRecord();
                record.setRecordId(signedData.getRecordIdList().get(i));
                record.setReceiveUserId(userId);
                record = recordService.readRecord(record);
                // 给好友发送签收消息
                sendMsg(signedData.getSendUserId(), "/chat/signed", NCResult.ok(record));
                // 给自己发送签收消息
                sendMsg(userId, "/chat/signed", NCResult.ok(record));
            }
            return;
        }
        throw new NCException("-1", "参数错误");
    }
}
