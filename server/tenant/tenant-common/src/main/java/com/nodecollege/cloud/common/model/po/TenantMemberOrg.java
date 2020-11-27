package com.nodecollege.cloud.common.model.po;

import lombok.Data;

/**
 * Table: t_member_org
 * 版权：节点学院
 *
 * @author LC
 * @date 2020-10-15 21:53:33
 */
@Data
public class TenantMemberOrg {
    // 主键
    private Long id;

    // 租户id
    private Long tenantId;

    // 成员id
    private Long memberId;

    // 机构代码
    private String orgCode;
}