package com.nodecollege.cloud.client.feign;

import com.nodecollege.cloud.client.fallback.SyncApiClientFallback;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.po.OperateAppApi;
import com.nodecollege.cloud.common.model.vo.DataPowerVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * @author LC
 * @date 2020/1/10 9:47
 */
@Primary
@FeignClient(contextId = "syncApiClient", value = "operate", fallback = SyncApiClientFallback.class)
public interface SyncApiClient {

    /**
     * 添加接口访问数据
     */
    @PostMapping("/appApi/initApiList")
    NCResult<OperateAppApi> initApiList(@RequestBody List<OperateAppApi> apiVisit);

    @PostMapping("/dataPower/getUserAuth")
    NCResult<String> getUserAuth(@RequestBody DataPowerVO dataPowerVO);
}
