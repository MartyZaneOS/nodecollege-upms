package com.nodecollege.cloud.feign;

import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.fallback.LogClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * @author LC
 * @date 2020/1/10 9:47
 */
@Primary
@FeignClient(value = "log", fallback = LogClientFallback.class)
public interface LogClient {

    /**
     * 系统日志入库
     */
    @PostMapping("/log/sysLogStorage")
    NCResult<Map<String, Integer>> sysLogStorage();
    /**
     * 删除系统日志
     */
    @PostMapping("/log/delSysLog")
    NCResult<Map<String, Integer>> delSysLog(@RequestBody Map<String, Integer> map);
}
