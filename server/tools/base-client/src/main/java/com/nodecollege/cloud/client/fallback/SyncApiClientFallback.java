package com.nodecollege.cloud.client.fallback;

import com.nodecollege.cloud.client.feign.syncApiClient;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.po.OperateAppApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author LC
 * @date 2020/1/10 9:50
 */
@Slf4j
@Component
public class SyncApiClientFallback implements syncApiClient {

    @Override
    public NCResult<OperateAppApi> initApiList(List<OperateAppApi> apiVisitList) {
        String message = "operate-initApiList 初始化接口List失败！中台熔断器——调用接口出现异常！";
        log.error(message);
        return NCResult.error("-1", message);
    }
}
