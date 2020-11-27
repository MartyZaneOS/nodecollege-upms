package com.nodecollege.cloud.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nodecollege.cloud.common.annotation.ApiAnnotation;
import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateConfig;
import com.nodecollege.cloud.common.model.po.OperateTenant;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.service.ConfigService;
import com.nodecollege.cloud.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author LC
 * @date 2020/2/8 15:26
 */
@RestController
@RequestMapping("/config")
public class ConfigController {

    @Autowired
    private ConfigService configService;

    @Autowired
    private TenantService tenantService;

    @ApiAnnotation(modularName = "配置管理", description = "查询配置列表")
    @PostMapping("/getConfigList")
    public NCResult<OperateConfig> getConfigList(@RequestBody QueryVO<OperateConfig> queryVO) {
        List<OperateConfig> list = new ArrayList<>();
        Long total = 0L;
        if (NCConstants.INT_NEGATIVE_1.equals(queryVO.getPageSize())) {
            list = configService.getConfigList(queryVO);
            total = NCUtils.isNullOrEmpty(list) ? 0 : Long.parseLong(list.size() + "");
        } else {
            Page page = PageHelper.startPage(queryVO.getPageNum(), queryVO.getPageSize());
            if (queryVO.isSort()) {
                page.setOrderBy(queryVO.getSortKey() + " " + queryVO.getSortDirection());
            }
            list = configService.getConfigList(queryVO);
            total = page.getTotal();
        }
        return NCResult.ok(list, total);
    }

    @ApiAnnotation(modularName = "配置管理", description = "添加配置")
    @PostMapping("/addConfig")
    public NCResult<OperateConfig> addConfig(@RequestBody OperateConfig config) {
        configService.addConfig(config);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "配置管理", description = "修改配置")
    @PostMapping("/editConfig")
    public NCResult<OperateConfig> editConfig(@RequestBody OperateConfig config) {
        configService.updateConfig(config);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "配置管理", description = "删除配置")
    @PostMapping("/delConfig")
    public NCResult<OperateConfig> delConfig(@RequestBody OperateConfig config) {
        configService.delConfig(config);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "租户配置管理", description = "查询租户配置列表")
    @PostMapping("/getTenantConfigList")
    public NCResult<OperateConfig> getTenantConfigList(@RequestBody OperateConfig query) {
        NCUtils.nullOrEmptyThrow(query.getTenantId());
        OperateTenant tenant = new OperateTenant();
        tenant.setTenantId(query.getTenantId());
        tenant = tenantService.getTenantById(tenant);
        NCUtils.nullOrEmptyThrow(tenant, "", "租户不存在！");

        QueryVO<OperateConfig> queryVO = new QueryVO<>(new OperateConfig());
        queryVO.getData().setConfigCode(query.getConfigCode());
        queryVO.getData().setConfigGroup(query.getConfigGroup());
        queryVO.setLongList(Arrays.asList(7L, 8L, 9L, 10L));
        // 查询预制配置
        queryVO.getData().setPreFlag(1);
        List<OperateConfig> list = configService.getConfigList(queryVO);

        // 查询租户自定义配置
        queryVO = new QueryVO<>(query);
        queryVO.getData().setTenantCode(tenant.getTenantCode());
        queryVO.getData().setPreFlag(null);
        list.addAll(configService.getConfigList(queryVO));

        Map<String, OperateConfig> map = new HashMap<>();
        for (OperateConfig config : list) {
            if (config.getPreFlag() != null && config.getPreFlag() == 1) {
                config.setConfigId(null);
            }
            String key = config.getConfigUsage() + "" + config.getConfigGroup() + "_" + config.getConfigCode();
            if (!map.containsKey(key) || map.get(key).getPreFlag() > config.getPreFlag()) {
                map.put(key, config);
            }
        }
        list = new ArrayList<>(map.values());
        list.sort((a, b) -> {
            if (a.getConfigGroup() == null || b.getConfigGroup() == null) {
                return -1;
            } else if (a.getConfigGroup().compareTo(b.getConfigGroup()) == 0){
                return a.getConfigCode().compareTo(b.getConfigCode());
            }
            return a.getConfigGroup().compareTo(b.getConfigGroup());
        });
        return NCResult.ok(list);
    }
}
