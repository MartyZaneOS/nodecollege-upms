package com.nodecollege.cloud.fallback;

import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateUser;
import com.nodecollege.cloud.feign.UserClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author LC
 * @date 2020/1/10 9:50
 */
@Slf4j
@Component
public class UserClientFallback implements UserClient {

    @Override
    public NCResult<OperateUser> getUserListByQuery(@RequestBody QueryVO queryVO) {
        String message = "operate-getUserListByQuery 系统日志入库！中台熔断器——调用接口出现异常！";
        log.error(message);
        return NCResult.error("-1", message);
    }

    @Override
    public NCResult<OperateUser> getUserByUserId(@RequestBody Long userId) {
        String message = "operate-getUserByUserId 删除系统日志！中台熔断器——调用接口出现异常！";
        log.error(message);
        return NCResult.error("-1", message);

    }
}
