package com.nodecollege.cloud.service.impl;

import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateTenant;
import com.nodecollege.cloud.common.model.po.OperateTenantApply;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.dao.mapper.OperateTenantApplyMapper;
import com.nodecollege.cloud.dao.mapper.OperateTenantMapper;
import com.nodecollege.cloud.feign.TenantClient;
import com.nodecollege.cloud.service.TenantApplyService;
import com.nodecollege.cloud.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author LC
 * @date 2020/8/28 20:38
 */
@Service
public class TenantApplyServiceImpl implements TenantApplyService {

    @Autowired
    private OperateTenantApplyMapper tenantApplyMapper;

    @Autowired
    private OperateTenantMapper tenantMapper;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private TenantClient tenantClient;

    @Override
    public List<OperateTenantApply> getTenantApplyList(QueryVO<OperateTenantApply> queryVO) {
        return tenantApplyMapper.selectListByMap(queryVO.toMap());
    }

    @Override
    public void addTenantApply(OperateTenantApply apply) {
        NCUtils.nullOrEmptyThrow(apply.getTenantCode());
        NCUtils.nullOrEmptyThrow(apply.getTenantName());
        NCUtils.nullOrEmptyThrow(apply.getTenantDesc());
        NCUtils.nullOrEmptyThrow(apply.getApplyUserId());
        NCUtils.nullOrEmptyThrow(apply.getApplyUserName());
        NCUtils.nullOrEmptyThrow(apply.getApplyEmail());

        QueryVO<OperateTenantApply> queryEx = new QueryVO<>(new OperateTenantApply());
        queryEx.getData().setTenantCode(apply.getTenantCode());
        queryEx.getData().setState(0);
        List<OperateTenantApply> exList = tenantApplyMapper.selectListByMap(queryEx.toMap());
        NCUtils.notNullOrNotEmptyThrow(exList, "", "该租户代码已存在！");

        List<OperateTenant> exTenantList = tenantMapper.selectTenantListByMap(queryEx.toMap());
        NCUtils.notNullOrNotEmptyThrow(exTenantList, "", "该租户代码已存在！");
        apply.setState(0);
        apply.setCreateTime(new Date());
        tenantApplyMapper.insertSelective(apply);
    }

    @Override
    public void editTenantApply(OperateTenantApply apply) {
        NCUtils.nullOrEmptyThrow(apply.getId());
        NCUtils.nullOrEmptyThrow(apply.getModifyName());
        NCUtils.nullOrEmptyThrow(apply.getState());
        OperateTenantApply ex = tenantApplyMapper.selectByPrimaryKey(apply.getId());
        NCUtils.nullOrEmptyThrow(ex, "", "该申请不存在！");
        if (ex.getState() != 0){
            throw new NCException("", "该申请非待审核状态！");
        }
        if (apply.getState() == -1){
            // 审核拒绝
            // todo 发送邮件
        } else if (apply.getState() == 1){
            // 审核通过
            OperateTenant tenant = new OperateTenant();
            tenant.setTenantCode(ex.getTenantCode());
            tenant.setTenantName(ex.getTenantName());
            tenant.setIntroduce(ex.getTenantDesc());
            tenant.setCreateUserId(ex.getApplyUserId());
            tenant = tenantService.addTenant(tenant);
            ex.setState(1);
            // todo 发送成功邮件
            // 初始化租户信息
            tenantClient.init(tenant);
        } else {
            throw new NCException("", "参数错误！");
        }
        OperateTenantApply update = new OperateTenantApply();
        update.setId(apply.getId());
        update.setState(apply.getState());
        update.setModifyName(apply.getModifyName());
        update.setModifyTime(new Date());
        tenantApplyMapper.updateByPrimaryKeySelective(update);
    }
}
