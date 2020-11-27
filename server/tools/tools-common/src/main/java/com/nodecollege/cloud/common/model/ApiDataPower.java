package com.nodecollege.cloud.common.model;

import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.utils.NCUtils;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 接口数据权限
 *
 * @Author LC
 * @Date 2019/8/9 9:55
 */
@Data
public class ApiDataPower {
    /**
     * 租户id
     */
    private Long tenantId;
    /**
     * 成员id
     */
    private Long memberId;
    /**
     * 能操作的组织机构列表
     * 为null，可以操作租户下所有组织机构数据
     * 为空, 只能操作当前登陆人的数据，memberId必填
     * 不为空，可以操作列表中组织机构的数据
     */
    private List<Long> orgList;

    /**
     * 校验数据权限
     *
     * @param tenantId
     * @param orgId
     * @param memberId
     */
    public void check(Long tenantId, Long orgId, Long memberId) {
        if (!this.tenantId.equals(tenantId)) {
            throw new NCException("-1", "没有操作该数据的权限!");
        }
        if (orgList == null) {
            return;
        }
        if (orgList.isEmpty()) {
            if (memberId == null || !this.memberId.equals(memberId)) {
                throw new NCException("-1", "没有操作该数据的权限!");
            }
        } else {
            if (!orgList.contains(orgId)) {
                throw new NCException("-1", "没有操作该数据的权限!");
            }
        }
    }

    /**
     * 校验数据权限
     *
     * @param baseModel
     */
    public void check(AbstractBaseModel baseModel) {
        check(baseModel.getTenantId(), baseModel.getOrgId(), baseModel.getMemberId());
    }

    /**
     * 处理请求数据
     * @param queryVO
     */
    public void handle(AbstractQueryVO queryVO){
        queryVO.setUserId(null);
        queryVO.setMemberId(null);
        if (this.orgList == null){
            return;
        }
        if (!this.orgList.isEmpty()){
            if (NCUtils.isNotNullOrNotEmpty(queryVO.getOrgList())){
                queryVO.getOrgList().retainAll(this.orgList);
            }else {
                queryVO.setOrgList(new ArrayList<>(this.orgList));
            }
        }else {
            queryVO.setMemberId(this.memberId);
        }
    }
}
