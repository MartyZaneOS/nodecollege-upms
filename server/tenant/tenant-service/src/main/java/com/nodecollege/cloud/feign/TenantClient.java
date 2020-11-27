package com.nodecollege.cloud.feign;

import com.nodecollege.cloud.common.model.MenuVO;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.*;
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
@FeignClient(value = "operate", fallback = TenantClientFallback.class)
public interface TenantClient {

    /**
     * 获取用户信息根据用户id
     */
    @PostMapping("/user/getUserByUserId")
    NCResult<OperateUser> getUserListByUserId(@RequestBody Long userId);

    /**
     * 邀请成员
     */
    @PostMapping("/invite/inviteMember")
    NCResult<OperateUser> inviteUser(@RequestBody OperateUserInvite inviteUser);

    /**
     * 查询租户信息
     */
    @PostMapping("/tenant/getTenantInfo")
    NCResult<OperateTenant> getTenantInfo(@RequestBody OperateTenant tenant);

    /**
     * 查询租户所有菜单
     */
    @PostMapping("/tenant/getTenantMenu")
    NCResult<MenuVO> getTenantMenu(@RequestBody OperateTenant tenant);

    /**
     * 查询租户预制角色
     */
    @PostMapping("/tenant/getTenantRole")
    NCResult<OperateRole> getTenantRole(@RequestBody OperateTenant tenant);

    /**
     * 查询租户预制角色
     */
    @PostMapping("/tenant/role/getTenantRoleMenuList")
    NCResult<OperateRoleMenu> getTenantRoleMenuList(@RequestBody QueryVO<OperateRoleMenu> queryVO);

    /**
     * 查询配置信息
     */
    @PostMapping("/config/getTenantConfigList")
    NCResult<OperateConfig> getTenantConfigList(@RequestBody OperateConfig queryVO);
}
