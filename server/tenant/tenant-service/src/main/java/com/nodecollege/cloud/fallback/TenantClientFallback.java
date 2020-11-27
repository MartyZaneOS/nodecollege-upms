package com.nodecollege.cloud.fallback;

import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.MenuVO;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.*;
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
    public NCResult<OperateUser> getUserListByUserId(Long userId) {
        String message = "operate-getUserListByUserId 查询用户信息！中台熔断器——调用接口出现异常！";
        log.error(message);
        return NCResult.error("-1", message);
    }

    @Override
    public NCResult<OperateTenant> getTenantInfo(OperateTenant tenant) {
        String message = "operate-getTenantInfo 查询租户信息！中台熔断器——调用接口出现异常！";
        log.error(message);
        return NCResult.error("-1", message);
    }

    @Override
    public NCResult<OperateUser> inviteUser(OperateUserInvite inviteUser) {
        String message = "operate-inviteUser 查询用户信息！中台熔断器——调用接口出现异常！";
        log.error(message);
        return NCResult.error("-1", message);
    }

    @Override
    public NCResult<MenuVO> getTenantMenu(@RequestBody OperateTenant tenant) {
        String message = "operate-getTenantMenu 查询租户菜单！中台熔断器——调用接口出现异常！";
        log.error(message);
        return NCResult.error("-1", message);
    }

    @Override
    public NCResult<OperateRole> getTenantRole(@RequestBody OperateTenant tenant) {
        String message = "operate-getTenantRole 查询租户预制角色！中台熔断器——调用接口出现异常！";
        log.error(message);
        return NCResult.error("-1", message);

    }

    @Override
    public NCResult<OperateRoleMenu> getTenantRoleMenuList(QueryVO<OperateRoleMenu> queryVO) {
        String message = "operate-getTenantRoleMenuList 查询租户预制角色菜单！中台熔断器——调用接口出现异常！";
        log.error(message);
        return NCResult.error("-1", message);
    }

    @Override
    public NCResult<OperateConfig> getTenantConfigList(OperateConfig queryVO) {
        String message = "operate-getConfigList 查询配置列表！中台熔断器——调用接口出现异常！";
        log.error(message);
        return NCResult.error("-1", message);
    }
}
