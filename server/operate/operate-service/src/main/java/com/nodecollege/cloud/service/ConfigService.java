package com.nodecollege.cloud.service;

import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateConfig;

import java.util.List;

/**
 * @author LC
 * @date 2020/2/7 22:05
 */
public interface ConfigService {

    /**
     * 获取配置信息
     */
    List<OperateConfig> getConfigList(QueryVO<OperateConfig> queryVO);

    /**
     * 根据配置代码获取配置缓存
     */
    OperateConfig getConfigCacheByCode(Integer configUsage, String configCode);

    /**
     * 根据配置分组获取配置列表缓存
     */
    List<OperateConfig> getConfigListCacheByGroup(Integer configUsage, String configGroup);

    /**
     * 清除配置缓存
     */
    void cleanConfigCache();

    /**
     * 添加配置信息
     */
    void addConfig(OperateConfig config);

    /**
     * 更新配置信息
     */
    void updateConfig(OperateConfig config);

    /**
     * 删除配置文件
     */
    void delConfig(OperateConfig config);
}
