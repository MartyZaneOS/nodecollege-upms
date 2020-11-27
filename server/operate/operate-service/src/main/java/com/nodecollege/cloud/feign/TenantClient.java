package com.nodecollege.cloud.feign;

import com.nodecollege.cloud.common.model.MenuVO;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.po.OperateRole;
import com.nodecollege.cloud.common.model.po.OperateTenant;
import com.nodecollege.cloud.common.model.po.OperateUser;
import com.nodecollege.cloud.common.model.po.OperateUserInvite;
import com.nodecollege.cloud.fallback.TenantClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author LC
 * @date 2020/1/10 9:47
 */
@Primary
@FeignClient(value = "tenant", fallback = TenantClientFallback.class)
public interface TenantClient {

    @PostMapping("/tenant/init")
    NCResult init(@RequestBody OperateTenant tenant);

    @PostMapping("/member/inviteMemberSuccess")
    NCResult inviteMemberSuccess(@RequestBody OperateUser user);
}
