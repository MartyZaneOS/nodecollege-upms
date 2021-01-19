package com.nodecollege.cloud.fallback;

import com.nodecollege.cloud.common.enums.ErrorEnum;
import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.po.SysVisitLog;
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
    public NCResult<Map<String, Integer>> delSysLog(@RequestBody Map<String, Integer> map) {
        log.error("log-delSysLog 删除系统日志！中台熔断器——调用接口出现异常！");
        throw new NCException(ErrorEnum.FEIGN_SERVICE_ERROR);
    }

    @Override
    public NCResult<Map<String, Integer>> countVisitData(SysVisitLog visitLog) {
        log.error("log-countVisitData 统计访问数据 调用接口出现异常！");
        throw new NCException(ErrorEnum.FEIGN_SERVICE_ERROR);
    }
}
