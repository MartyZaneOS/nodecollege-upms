package com.nodecollege.cloud.feign;

import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.po.ChatDebate;
import com.nodecollege.cloud.fallback.WorldTreeClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author LC
 * @date 22:58 2020/4/22
 **/
@Primary
@FeignClient(value = "worldTree", fallback = WorldTreeClientFallback.class)
public interface WorldTreeClient {

    /**
     * 处理辩论堂数据
     */
    @PostMapping("/basic/debateHandle")
    NCResult debateHandle(@RequestBody ChatDebate debate);

}
