package com.nodecollege.cloud.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nodecollege.cloud.client.utils.NCLoginUtils;
import com.nodecollege.cloud.common.annotation.ApiAnnotation;
import com.nodecollege.cloud.common.enums.ErrorEnum;
import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.BindVO;
import com.nodecollege.cloud.common.model.NCLoginUserVO;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateTenantApply;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.service.TenantApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author LC
 * @date 2020/8/28 20:36
 */
@RestController
@RequestMapping("/tenantApply")
public class TenantApplyController {

    @Autowired
    private TenantApplyService tenantApplyService;

    @Autowired
    private NCLoginUtils loginUtils;

    @ApiAnnotation(modularName = "租户申请", description = "查询用户租户申请列表")
    @PostMapping("/getTenantApplyListByUserId")
    public NCResult<OperateTenantApply> getTenantApplyListByUserId (@RequestBody QueryVO<OperateTenantApply> queryVO) {
        NCLoginUserVO loginUser = loginUtils.getUserLoginInfo();
        NCUtils.nullOrEmptyThrow(loginUser, ErrorEnum.LOGIN_TIME_OUT);
        queryVO.getData().setApplyUserId(loginUser.getLoginId());
        Page page = PageHelper.startPage(queryVO.getPageNum(), queryVO.getPageSize());
        List<OperateTenantApply> list = tenantApplyService.getTenantApplyList(queryVO);
        return NCResult.ok(list, page.getTotal());
    }

    @ApiAnnotation(modularName = "租户申请", description = "添加租户申请")
    @PostMapping("/addTenantApply")
    public NCResult addTenantApply (@RequestBody OperateTenantApply apply) {
        NCLoginUserVO loginUser = loginUtils.getUserLoginInfo();
        NCUtils.nullOrEmptyThrow(loginUser, ErrorEnum.LOGIN_TIME_OUT);
        apply.setApplyUserId(loginUser.getLoginId());
        apply.setApplyUserName(loginUser.getAccount());
        tenantApplyService.addTenantApply(apply);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "租户申请", description = "查询所有租户申请列表")
    @PostMapping("/getTenantApplyList")
    public NCResult<OperateTenantApply> getTenantApplyList (@RequestBody QueryVO<OperateTenantApply> queryVO) {
        Page page = PageHelper.startPage(queryVO.getPageNum(), queryVO.getPageSize());
        List<OperateTenantApply> list = tenantApplyService.getTenantApplyList(queryVO);
        return NCResult.ok(list, page.getTotal());
    }

    @ApiAnnotation(modularName = "租户申请", description = "审核租户申请")
    @PostMapping("/editTenantApply")
    public NCResult editTenantApply (@RequestBody OperateTenantApply apply) {
        NCLoginUserVO loginAdmin = loginUtils.getAdminLoginInfo();
        NCUtils.nullOrEmptyThrow(loginAdmin, ErrorEnum.LOGIN_TIME_OUT);
        apply.setModifyName(loginAdmin.getAccount());
        tenantApplyService.editTenantApply(apply);
        return NCResult.ok();
    }

}
