package com.nodecollege.cloud.feign;

import com.nodecollege.cloud.common.model.DebateDTO;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.po.ChatDebate;
import com.nodecollege.cloud.fallback.ChatDebateClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author LC
 * @date 2020/1/10 9:47
 */
@Primary
@FeignClient(value = "chat", fallback = ChatDebateClientFallback.class)
public interface ChatDebateClient {

    /**
     * 添加辩论堂
     */
    @PostMapping("/debate/addDebateApi")
    NCResult<ChatDebate> addDebateApi(@RequestBody DebateDTO debate);
}
