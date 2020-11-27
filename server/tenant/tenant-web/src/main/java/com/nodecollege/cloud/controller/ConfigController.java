package com.nodecollege.cloud.controller;

import com.nodecollege.cloud.client.utils.NCLoginUtils;
import com.nodecollege.cloud.common.annotation.ApiAnnotation;
import com.nodecollege.cloud.common.enums.ErrorEnum;
import com.nodecollege.cloud.common.model.NCLoginUserVO;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.po.OperateConfig;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author LC
 * @date 2020/11/25 21:36
 */
@RestController
@RequestMapping("/config")
public class ConfigController {

    @Autowired
    private ConfigService configService;

    @Autowired
    private NCLoginUtils loginUtils;

    @ApiAnnotation(modularName = "配置管理", description = "查询租户配置信息")
    @PostMapping("/getConfigList")
    public NCResult<OperateConfig> getConfigList(@RequestBody OperateConfig query) {
        NCLoginUserVO loginMember = loginUtils.getMemberLoginInfo();
        NCUtils.nullOrEmptyThrow(loginMember, ErrorEnum.LOGIN_TIME_OUT);
        query.setTenantId(loginMember.getTenantId());
        return NCResult.ok(configService.getConfigList(query));
    }

}
