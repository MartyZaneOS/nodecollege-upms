package com.nodecollege.cloud.fallback;

import com.nodecollege.cloud.common.enums.ErrorEnum;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.po.OperateTenant;
import com.nodecollege.cloud.common.model.po.OperateUser;
import com.nodecollege.cloud.feign.TenantClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author LC
 * @date 2020/1/10 9:50
 */
@Slf4j
@Component
public class TenantClientFallback implements TenantClient {

    @Override
    public NCResult init(@RequestBody OperateTenant tenant) {
        String message = "tenant-init 查询用户信息！中台熔断器——调用接口出现异常！";
        log.error(message);
        return NCResult.error(ErrorEnum.FEIGN_SERVICE_ERROR);
    }

    @Override
    public NCResult inviteMemberSuccess(@RequestBody OperateUser user) {
        String message = "tenant-inviteMemberSuccess 查询用户信息！中台熔断器——调用接口出现异常！";
        log.error(message);
        return NCResult.error(ErrorEnum.FEIGN_SERVICE_ERROR);
    }
}
