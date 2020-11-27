package com.nodecollege.cloud.service;

import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateConfig;

import java.util.List;

/**
 * @author LC
 * @date 2020/10/22 17:14
 */
public interface ConfigService {

    List<OperateConfig> getConfigList(OperateConfig query);

    /**
     * 根据配置代码获取配置缓存
     */
    OperateConfig getConfigCacheByCode(OperateConfig query);

    /**
     * 根据配置分组获取配置列表缓存
     */
    List<OperateConfig> getConfigListCacheByGroup(OperateConfig query);
}
