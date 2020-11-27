package com.nodecollege.cloud.controller;

import com.nodecollege.cloud.client.utils.NCLoginUtils;
import com.nodecollege.cloud.common.annotation.ApiAnnotation;
import com.nodecollege.cloud.common.enums.ErrorEnum;
import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.MenuVO;
import com.nodecollege.cloud.common.model.NCLoginUserVO;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.po.OperateTenant;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.feign.TenantClient;
import com.nodecollege.cloud.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author LC
 * @date 2020/10/20 14:35
 */
@RestController
@RequestMapping("/tenant")
public class TenantController {

    @Autowired
    private TenantService tenantService;

    @Autowired
    private NCLoginUtils loginUtils;

    @Autowired
    private TenantClient tenantClient;

    @ApiAnnotation(modularName = "租户信息", description = "初始化租户信息")
    @PostMapping("/init")
    public NCResult init(@RequestBody OperateTenant tenant) {
        tenantService.init(tenant);
        return NCResult.ok("初始化租户成功！");
    }

    @ApiAnnotation(modularName = "租户信息", description = "查询租户菜单")
    @PostMapping("/getMenuTree")
    public NCResult<MenuVO> getMenuTree() {
        NCLoginUserVO loginMember = loginUtils.getMemberLoginInfo();
        NCUtils.nullOrEmptyThrow(loginMember, ErrorEnum.LOGIN_TIME_OUT);
        OperateTenant query = new OperateTenant();
        query.setTenantId(loginMember.getTenantId());
        NCResult<MenuVO> menuResult = tenantClient.getTenantMenu(query);
        if (!menuResult.getSuccess()) {
            throw new NCException(menuResult.getCode(), menuResult.getMessage());
        }
        if (menuResult.getRows() == null || menuResult.getRows().isEmpty()) {
            return NCResult.ok();
        }
        return NCResult.ok(MenuVO.getMenuTree(menuResult.getRows()));
    }
}
