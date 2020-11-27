package com.nodecollege.cloud.fallback;

import com.nodecollege.cloud.common.model.DebateDTO;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.po.ChatDebate;
import com.nodecollege.cloud.feign.ChatDebateClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author LC
 * @date 2020/4/19 17:59
 */
@Slf4j
@Component
public class ChatDebateClientFallback implements ChatDebateClient {
    @Override
    public NCResult<ChatDebate> addDebateApi(DebateDTO debate) {
        String message = "chat-addDebateApi 添加辩论堂失败！中台熔断器——调用接口出现异常！";
        log.error(message);
        return NCResult.error("-1", message);
    }

}
