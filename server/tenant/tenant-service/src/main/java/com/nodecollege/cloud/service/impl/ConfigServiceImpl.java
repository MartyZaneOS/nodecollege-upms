package com.nodecollege.cloud.service.impl;

import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.po.OperateConfig;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.feign.TenantClient;
import com.nodecollege.cloud.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LC
 * @date 2020/10/22 17:14
 */
@Service
public class ConfigServiceImpl implements ConfigService {

    @Autowired
    private TenantClient tenantClient;

    @Override
    public List<OperateConfig> getConfigList(OperateConfig query) {
        NCUtils.nullOrEmptyThrow(query.getTenantId());
        NCResult<OperateConfig> configResult = tenantClient.getTenantConfigList(query);
        if (!configResult.getSuccess()) {
            throw new NCException(configResult.getCode(), configResult.getMessage());
        }
        return configResult.getRows();
    }

    @Override
    public OperateConfig getConfigCacheByCode(OperateConfig query) {
        NCUtils.nullOrEmptyThrow(query.getConfigCode());
        List<OperateConfig> configList = getConfigList(query);
        if (configList.isEmpty()) {
            return null;
        }
        return configList.get(0);
    }

    @Override
    public List<OperateConfig> getConfigListCacheByGroup(OperateConfig query) {
        NCUtils.nullOrEmptyThrow(query.getConfigGroup());
        return getConfigList(query);
    }
}
