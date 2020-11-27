package com.nodecollege.cloud.feign;

import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateUser;
import com.nodecollege.cloud.fallback.UserClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author LC
 * @date 2020/1/10 9:47
 */
@Primary
@FeignClient(value = "operate", fallback = UserClientFallback.class)
public interface UserClient {

    /**
     * 系统日志入库
     */
    @PostMapping("/user/getUserListByQuery")
    NCResult<OperateUser> getUserListByQuery(@RequestBody QueryVO queryVO);

    /**
     * 删除系统日志
     */
    @PostMapping("/user/getUserByUserId")
    NCResult<OperateUser> getUserByUserId(@RequestBody Long userId);
}
