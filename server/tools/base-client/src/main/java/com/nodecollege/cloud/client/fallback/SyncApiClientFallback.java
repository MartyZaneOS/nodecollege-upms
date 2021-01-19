package com.nodecollege.cloud.client.fallback;

import com.nodecollege.cloud.client.feign.SyncApiClient;
import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.po.OperateAppApi;
import com.nodecollege.cloud.common.model.vo.DataPowerVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author LC
 * @date 2020/1/10 9:50
 */
@Slf4j
@Component
public class SyncApiClientFallback implements SyncApiClient {

    @Override
    public NCResult<OperateAppApi> initApiList(List<OperateAppApi> apiVisitList) {
        String message = "operate-initApiList 初始化接口List失败！中台熔断器——调用接口出现异常！";
        log.error(message);
        return NCResult.error("-1", message);
    }

    @Override
    public NCResult<String> getUserAuth(DataPowerVO dataPowerVO) {
        String message = "operate-getUserAuth 查询用户数据权限信息！中台熔断器——调用接口出现异常！";
        log.error(message);
        throw new NCException("", "接口调用失败！");
    }
}
