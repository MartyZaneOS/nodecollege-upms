package com.nodecollege.cloud.service;

import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateTenantApply;

import java.util.List;

/**
 * @author LC
 * @date 2020/8/28 20:38
 */
public interface TenantApplyService {
    /**
     * 查询租户申请列表
     */
    List<OperateTenantApply> getTenantApplyList(QueryVO<OperateTenantApply> queryVO);

    /**
     * 添加租户申请
     */
    void addTenantApply(OperateTenantApply apply);

    /**
     * 审核租户申请
     */
    void editTenantApply(OperateTenantApply apply);
}
