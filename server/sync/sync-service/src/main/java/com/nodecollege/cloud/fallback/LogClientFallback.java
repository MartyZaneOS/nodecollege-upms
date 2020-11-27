package com.nodecollege.cloud.fallback;

import com.nodecollege.cloud.common.enums.ErrorEnum;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.feign.LogClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * @author LC
 * @date 2020/1/10 9:50
 */
@Slf4j
@Component
public class LogClientFallback implements LogClient {

    @Override
    public NCResult<Map<String, Integer>> sysLogStorage() {
        String message = "log-sysLogStorage 系统日志入库！中台熔断器——调用接口出现异常！";
        log.error(message);
        return NCResult.error(ErrorEnum.FEIGN_SERVICE_ERROR);
    }

    @Override
    public NCResult<Map<String, Integer>> delSysLog(@RequestBody Map<String, Integer> map) {
        String message = "log-delSysLog 删除系统日志！中台熔断器——调用接口出现异常！";
        log.error(message);
        return NCResult.error(ErrorEnum.FEIGN_SERVICE_ERROR);

    }
}
