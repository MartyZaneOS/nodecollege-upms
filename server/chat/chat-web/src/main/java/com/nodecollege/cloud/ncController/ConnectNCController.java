package com.nodecollege.cloud.ncController;

import com.nodecollege.cloud.common.annotation.NCController;
import com.nodecollege.cloud.common.annotation.NCRequestMapping;
import com.nodecollege.cloud.common.enums.ErrorEnum;
import com.nodecollege.cloud.common.model.NCLoginUserVO;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.UnreadEvent;
import com.nodecollege.cloud.common.model.vo.ChatActiveData;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.core.netty.ChatAbstrat;
import com.nodecollege.cloud.service.RecordService;
import com.nodecollege.cloud.utils.ChatLoginUtils;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

/**
 * 第一次(或重连)初始化连接
 *
 * @author LC
 * @date 2020/2/26 23:07
 */
@Slf4j
@NCController
@NCRequestMapping(value = "/chat")
public class ConnectNCController extends ChatAbstrat {

    @Autowired
    private RecordService recordService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private ChatLoginUtils chatLoginUtils;

    /**
     * 初次连接
     *
     * @param channel
     */
    @NCRequestMapping(value = "/connect")
    public NCResult connect1(Channel channel) {
        log.info("ChatHandlerConnectServiceImpl handler");
        NCLoginUserVO loginUser = chatLoginUtils.getUserLoginInfo();
        NCUtils.nullOrEmptyThrow(loginUser, ErrorEnum.LOGIN_TIME_OUT);
        QueryVO<Long> queryActive = new QueryVO<>();
        queryActive.setData(loginUser.getLoginId());
        List<ChatActiveData> activeDataList = recordService.getActiveChat(queryActive);
        // 保持用户连接状态
        connect(channel);
        for (int i = 0; i < activeDataList.size(); i++) {
            activeDataList.get(i).setUserId(queryActive.getData());
        }
        UnreadEvent unreadEvent = new UnreadEvent(this, activeDataList);
        applicationEventPublisher.publishEvent(unreadEvent);
        return NCResult.ok(activeDataList);
    }
}
