package com.nodecollege.cloud.service.impl;

import com.alibaba.fastjson.JSON;
import com.nodecollege.cloud.client.utils.NCLoginUtils;
import com.nodecollege.cloud.common.constants.ConfigConstants;
import com.nodecollege.cloud.common.constants.RedisConstants;
import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.NCLoginUserVO;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateConfig;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.common.utils.RedisUtils;
import com.nodecollege.cloud.cache.ConfigCache;
import com.nodecollege.cloud.dao.mapper.OperateConfigMapper;
import com.nodecollege.cloud.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @author LC
 * @date 2020/2/7 22:12
 */
@Service
public class ConfigServiceImpl implements ConfigService {

    @Autowired
    private OperateConfigMapper configMapper;

    @Autowired
    private NCLoginUtils loginUtils;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 查询配置列表
     */
    @Override
    public List<OperateConfig> getConfigList(QueryVO<OperateConfig> queryVO) {
        return configMapper.selectListByMap(queryVO.toMap());
    }

    @Override
    public OperateConfig getConfigCacheByCode(Integer configUsage, String configCode) {
        NCUtils.nullOrEmptyThrow(configUsage, "", "配置用途必填！");
        NCUtils.nullOrEmptyThrow(configCode, "", "配置代码必填！");
        String key = RedisConstants.CONFIG_CODE + configUsage + "_" + configCode;
        OperateConfig config = redisUtils.get(key, OperateConfig.class);
        if (config == null) {
            QueryVO<OperateConfig> queryVO = new QueryVO<>(new OperateConfig());
            queryVO.getData().setConfigUsage(configUsage);
            queryVO.getData().setConfigCode(configCode);
            List<OperateConfig> list = configMapper.selectListByMap(queryVO.toMap());
            if (!list.isEmpty()) {
                config = list.get(0);
                redisUtils.set(key, config, 60 * 60 * 2 + new Random().nextInt(60 * 10));
            }
        }
        return config;
    }

    @Override
    public List<OperateConfig> getConfigListCacheByGroup(Integer configUsage, String configGroup) {
        NCUtils.nullOrEmptyThrow(configUsage, "", "配置用途必填！");
        NCUtils.nullOrEmptyThrow(configGroup, "", "配置分组必填！");
        String key = RedisConstants.CONFIG_GROUP + configUsage + "_" + configGroup;
        List<OperateConfig> configList = redisUtils.getList(key, OperateConfig.class);
        if (configList == null) {
            QueryVO<OperateConfig> queryVO = new QueryVO<>(new OperateConfig());
            queryVO.getData().setConfigUsage(configUsage);
            queryVO.getData().setConfigGroup(configGroup);
            configList = configMapper.selectListByMap(queryVO.toMap());
            redisUtils.set(key, configList, 60 * 60 * 2 + new Random().nextInt(60 * 10));
        }
        return configList;
    }

    @Override
    public void cleanConfigCache() {
        redisUtils.delete(redisUtils.keys(RedisConstants.CONFIG_CODE + "*"));
        redisUtils.delete(redisUtils.keys(RedisConstants.CONFIG_GROUP + "*"));
        initConfigData();
    }

    /**
     * 添加配置信息
     */
    @Override
    public void addConfig(OperateConfig config) {
        NCUtils.nullOrEmptyThrow(config.getConfigName());
        NCUtils.nullOrEmptyThrow(config.getConfigCode());
        NCUtils.nullOrEmptyThrow(config.getConfigValue());
        NCUtils.nullOrEmptyThrow(config.getConfigUsage());
        NCUtils.nullOrEmptyThrow(config.getConfigType());

        Integer configUsage = config.getConfigUsage();

        OperateConfig query = new OperateConfig();
        query.setConfigUsage(configUsage);
        query.setConfigCode(config.getConfigCode());

        // powerOrgList 等于 null 表示只能操作自己的数据
        List<String> powerOrgList = null;
        NCLoginUserVO loginUser = null;
        if (configUsage == 0) {
            loginUser = loginUtils.getAdminLoginInfo();
            NCUtils.nullOrEmptyThrow(loginUser, "", "管理员未登陆");
        }
        if (configUsage == 1 || configUsage == 3) {
            NCUtils.nullOrEmptyThrow(config.getAdminOrgCode());
            powerOrgList = loginUtils.getPowerAdminOrgCodeList();
            if (powerOrgList == null || !powerOrgList.contains(config.getAdminOrgCode())) {
                throw new NCException("", "不能添加该管理员机构的配置信息！");
            }
            loginUser = loginUtils.getAdminLoginInfo();
            query.setAdminOrgCode(config.getAdminOrgCode());
            config.setAdminOrgCode(config.getAdminOrgCode());
        }
        if (configUsage == 2 || configUsage == 3) {
            loginUser = loginUtils.getAdminLoginInfo();
            NCUtils.nullOrEmptyThrow(loginUser, "", "管理员未登陆");
            query.setAdminAccount(loginUser.getAccount());
            config.setAdminAccount(loginUser.getAccount());
        }
        if (configUsage == 4 || configUsage == 6) {
            NCUtils.nullOrEmptyThrow(config.getUserOrgCode());
            powerOrgList = loginUtils.getPowerUserOrgCodeList();
            if (powerOrgList == null || !powerOrgList.contains(config.getUserOrgCode())) {
                throw new NCException("", "不能添加该用户机构的配置信息！");
            }
            loginUser = loginUtils.getUserLoginInfo();
            query.setUserOrgCode(config.getUserOrgCode());
            config.setUserOrgCode(config.getUserOrgCode());
        }
        if (configUsage == 5 || configUsage == 6) {
            NCUtils.nullOrEmptyThrow(config.getUserAccount());
            loginUser = loginUtils.getUserLoginInfo();
            NCUtils.nullOrEmptyThrow(loginUser, "", "用户未登陆");
            query.setUserAccount(loginUser.getAccount());
            config.setUserAccount(loginUser.getAccount());
        }
        if (configUsage >= 7 && configUsage <= 10) {
            loginUser = loginUtils.getMemberLoginInfo();
            NCUtils.nullOrEmptyThrow(loginUser, "", "租户成员未登陆");
            query.setTenantCode(loginUser.getTenantCode());
            config.setTenantCode(loginUser.getTenantCode());
        }
        if (configUsage == 8 || configUsage == 9) {
            NCUtils.nullOrEmptyThrow(config.getTenantOrgCode());
            powerOrgList = loginUtils.getPowerMemberOrgCodeList();
            if (powerOrgList == null || !powerOrgList.contains(config.getTenantOrgCode())) {
                throw new NCException("", "不能添加该租户机构的配置信息！");
            }
            query.setTenantOrgCode(config.getTenantOrgCode());
            config.setTenantOrgCode(config.getTenantOrgCode());
        }
        if (configUsage == 10 || configUsage == 9) {
            NCUtils.nullOrEmptyThrow(config.getMemberAccount());
            loginUser = loginUtils.getMemberLoginInfo();
            query.setMemberAccount(loginUser.getAccount());
            config.setMemberAccount(loginUser.getAccount());
        }

        List<OperateConfig> exList = configMapper.selectListByMap(new QueryVO<>(query).toMap());
        NCUtils.notNullOrNotEmptyThrow(exList, new NCException("", "该配置代码已使用！"));

        config.setState(1);
        config.setCreateUser(loginUser.getAccount());
        config.setCreateTime(new Date());
        config.setPreFlag(0);
        configMapper.insertSelective(config);
        cleanConfigCache();
    }

    /**
     * 更新配置信息
     *
     * @param config
     */
    @Override
    public void updateConfig(OperateConfig config) {
        NCUtils.nullOrEmptyThrow(config.getConfigId(), "", "配置id必填！");

        config.setConfigCode(null);
        config.setConfigUsage(null);
        config.setState(null);
        config.setCreateUser(null);
        config.setCreateTime(null);

        OperateConfig ex = configMapper.selectByPrimaryKey(config.getConfigId());
        NCUtils.nullOrEmptyThrow(ex, "", "该配置不存在！");

        Integer configUsage = ex.getConfigUsage();

        // powerOrgList 等于 null 表示只能操作自己的数据
        List<String> powerOrgList = null;
        NCLoginUserVO loginUser = null;

        // 只有管理员可以修改预制配置信息
        if (ex.getPreFlag() == 1) {
            loginUser = loginUtils.getAdminLoginInfo();
            NCUtils.nullOrEmptyThrow(loginUser, "", "管理员未登陆");

            config.setUpdateUser(loginUser.getAccount());
            config.setUpdateTime(new Date());
            configMapper.updateByPrimaryKeySelective(config);
            cleanConfigCache();
            return;
        }

        if (configUsage == 0) {
            loginUser = loginUtils.getAdminLoginInfo();
            NCUtils.nullOrEmptyThrow(loginUser, "", "管理员未登陆");
        }
        if (configUsage == 1 || configUsage == 3) {
            powerOrgList = loginUtils.getPowerAdminOrgCodeList();
            if (powerOrgList == null || !powerOrgList.contains(ex.getAdminOrgCode())) {
                throw new NCException("", "不能修改该管理员机构的配置信息！");
            }
            loginUser = loginUtils.getAdminLoginInfo();
        }
        if (configUsage == 2 || configUsage == 3) {
            loginUser = loginUtils.getAdminLoginInfo();
            NCUtils.nullOrEmptyThrow(loginUser, "", "管理员未登陆");
            if (!ex.getAdminAccount().equals(loginUser.getAccount())) {
                throw new NCException("", "不能修改该管理员的配置信息！");
            }
        }
        if (configUsage == 4 || configUsage == 6) {
            powerOrgList = loginUtils.getPowerUserOrgCodeList();
            if (powerOrgList == null || !powerOrgList.contains(config.getUserOrgCode())) {
                throw new NCException("", "不能修改该用户机构的配置信息！");
            }
            loginUser = loginUtils.getUserLoginInfo();
        }
        if (configUsage == 5 || configUsage == 6) {
            loginUser = loginUtils.getUserLoginInfo();
            NCUtils.nullOrEmptyThrow(loginUser, "", "用户未登陆");
            if (!ex.getUserAccount().equals(loginUser.getAccount())) {
                throw new NCException("", "不能修改该用户的配置信息！");
            }
        }
        if (configUsage >= 7 && configUsage <= 10) {
            loginUser = loginUtils.getMemberLoginInfo();
            NCUtils.nullOrEmptyThrow(loginUser, "", "租户成员未登陆");
            if (!ex.getTenantCode().equals(loginUser.getTenantCode())) {
                throw new NCException("", "不能修改该租户的配置信息！");
            }
        }
        if (configUsage == 8 || configUsage == 9) {
            powerOrgList = loginUtils.getPowerMemberOrgCodeList();
            if (powerOrgList == null || !powerOrgList.contains(config.getTenantOrgCode())) {
                throw new NCException("", "不能修改该租户机构的配置信息！");
            }
        }
        if (configUsage == 10 || configUsage == 9) {
            loginUser = loginUtils.getMemberLoginInfo();
            if (!ex.getMemberAccount().equals(loginUser.getAccount())) {
                throw new NCException("", "不能修改该租户成员的配置信息！");
            }
        }

        config.setUpdateUser(loginUser.getAccount());
        config.setUpdateTime(new Date());
        configMapper.updateByPrimaryKeySelective(config);
        cleanConfigCache();
    }

    /**
     * 删除配置信息
     *
     * @param config
     */
    @Override
    public void delConfig(OperateConfig config) {
        NCUtils.nullOrEmptyThrow(config.getConfigId());

        OperateConfig ex = configMapper.selectByPrimaryKey(config.getConfigId());
        NCUtils.nullOrEmptyThrow(ex, new NCException("", "该配置不存在！"));
        if (ex.getState() == 0) {
            throw new NCException("", "该配置不能删除！");
        }

        configMapper.deleteByPrimaryKey(config.getConfigId());
        cleanConfigCache();
    }

    /**
     * 初始化配置常量
     */
    @PostConstruct
    private void initConfigData() {
        // 平台默认密码
        OperateConfig config = getConfigCacheByCode(0, ConfigConstants.DEFAULT_PWD);
        if (config != null) {
            ConfigCache.DEFAULT_PWD = config.getConfigValue();
        }

        // 用户注册默认授权机构
        config = getConfigCacheByCode(0, "DEFAULT_USER_ORG");
        if (config != null) {
            List<String> orgs = JSON.parseArray(config.getConfigValue(), String.class);
            ConfigCache.DEFAULT_USER_ORG_LIST = orgs;
        }

        // 租户注册默认开通产品
        config = getConfigCacheByCode(0, "DEFAULT_TENANT_PRODUCT");
        if (config != null) {
            List<String> orgs = JSON.parseArray(config.getConfigValue(), String.class);
            ConfigCache.DEFAULT_TENANT_PRODUCT = orgs;
        }

        // 更新密码策略
        List<OperateConfig> configList = getConfigListCacheByGroup(0, "PASSWORD_POLICY");
        for (int i = 0; i < configList.size(); i++) {
            if ("CONTAIN_CAPITAL_ENGLISH".equals(configList.get(i).getConfigCode())) {
                ConfigCache.CONTAIN_CAPITAL_ENGLISH = Integer.valueOf(configList.get(i).getConfigValue());
            }
            if ("CONTAIN_LOWERCASE_ENGLISH".equals(configList.get(i).getConfigCode())) {
                ConfigCache.CONTAIN_LOWERCASE_ENGLISH = Integer.valueOf(configList.get(i).getConfigValue());
            }
            if ("CONTAIN_NUMBER".equals(configList.get(i).getConfigCode())) {
                ConfigCache.CONTAIN_NUMBER = Integer.valueOf(configList.get(i).getConfigValue());
            }
            if ("CONTAIN_SPECIAL".equals(configList.get(i).getConfigCode())) {
                ConfigCache.CONTAIN_SPECIAL = Integer.valueOf(configList.get(i).getConfigValue());
            }
            if ("PWD_EQUAL_USER_CHECK".equals(configList.get(i).getConfigCode())) {
                ConfigCache.PWD_EQUAL_USER_CHECK = Boolean.valueOf(configList.get(i).getConfigValue());
            }
            if ("PASSWORD_VALIDITY".equals(configList.get(i).getConfigCode())) {
                ConfigCache.PASSWORD_VALIDITY = Integer.valueOf(configList.get(i).getConfigValue());
            }
            if ("PASSWORD_LENGTH".equals(configList.get(i).getConfigCode())) {
                ConfigCache.PASSWORD_LENGTH = Integer.valueOf(configList.get(i).getConfigValue());
            }
            if ("HISTORY_SIZE".equals(configList.get(i).getConfigCode())) {
                ConfigCache.HISTORY_SIZE = Integer.valueOf(configList.get(i).getConfigValue());
            }
            if ("FIRST_LOGIN_CHECK".equals(configList.get(i).getConfigCode())) {
                ConfigCache.FIRST_LOGIN_CHECK = Boolean.valueOf(configList.get(i).getConfigValue());
            }
            if ("LOCK_THRESHOLD".equals(configList.get(i).getConfigCode())) {
                ConfigCache.LOCK_THRESHOLD = Integer.valueOf(configList.get(i).getConfigValue());
            }
            if ("CHECK_THRESHOLD".equals(configList.get(i).getConfigCode())) {
                ConfigCache.CHECK_THRESHOLD = Integer.valueOf(configList.get(i).getConfigValue());
            }
        }
    }
}
