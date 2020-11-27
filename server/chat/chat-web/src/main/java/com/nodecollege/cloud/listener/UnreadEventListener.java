package com.nodecollege.cloud.listener;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.UnreadEvent;
import com.nodecollege.cloud.common.model.po.ChatGroupUser;
import com.nodecollege.cloud.common.model.po.ChatRecord;
import com.nodecollege.cloud.common.model.vo.ChatActiveData;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.core.netty.ChatAbstrat;
import com.nodecollege.cloud.dao.mapper.ChatGroupUserMapper;
import com.nodecollege.cloud.dao.mapper.ChatRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 未读消息监听事件
 *
 * @author LC
 * @date 2020/3/17 20:38
 */
@Slf4j
@Component
public class UnreadEventListener extends ChatAbstrat {

    @Autowired
    private ChatRecordMapper recordMapper;

    @Autowired
    private ChatGroupUserMapper groupUserMapper;

    @Async("asyncPoolTaskExecutor")
    @EventListener
    public void unReadEventListener(UnreadEvent unreadEvent) {
        List<ChatActiveData> list = unreadEvent.getActiveDataList();
        log.info("未读事件监听{}", JSONObject.toJSONString(list));
        // 计算每个的最大未读数量
        for (int i = 0; i < list.size(); i++){
            ChatActiveData data = list.get(i);
            // 最大未读时间
            QueryVO<ChatRecord> queryVO = new QueryVO(new ChatRecord());
            if (data.getFriendId() != null) {
                queryVO.getData().setSendUserId(data.getFriendId());
                queryVO.getData().setReceiveUserId(data.getUserId());
                PageHelper.startPage(1, 1);
                List<ChatRecord> maxReceiveTimeList = recordMapper.selectFriendMaxReceiveTimeRecord(queryVO.toMap());
                if (maxReceiveTimeList.isEmpty()){
                    data.setNewsNum(NCConstants.INT_0);
                    continue;
                }
                queryVO.getData().setRecordId(maxReceiveTimeList.get(0).getRecordId());
                data.setNewsNum(recordMapper.selectChatNewsNum(queryVO.toMap()));
            } else {
                queryVO.getData().setGroupId(data.getGroupId());
                queryVO.setUserId(data.getUserId());
                List<ChatGroupUser> groupUserList = groupUserMapper.selectGroupUserList(queryVO.toMap());
                NCUtils.nullOrEmptyThrow(groupUserList, new NCException("-1", "未找到群用户信息！"));

                queryVO.getData().setReceiveTime(groupUserList.get(0).getRecordReadTime());
                data.setNewsNum(recordMapper.selectChatNewsNum(queryVO.toMap()));
            }
        }
        if (list.isEmpty()) {
            return;
        }
        sendMsg(list.get(0).getUserId(), "/chat/unreadNum", NCResult.ok(list));
    }
}
