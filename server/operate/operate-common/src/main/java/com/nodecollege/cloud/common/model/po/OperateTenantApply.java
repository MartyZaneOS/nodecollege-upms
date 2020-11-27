package com.nodecollege.cloud.common.model.po;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.utils.DateUtils;
import lombok.Data;

/**
 * Table: o_tenant_apply
 * 版权：节点学院
 *
 * @author LC
 * @date 2020-08-28 20:08:31
 */
@Data
public class OperateTenantApply {
    /**
     * 主键
     */
    private Long id;

    /**
     * 租户名称
     */
    private String tenantName;

    /**
     * 租户代码
     */
    private String tenantCode;

    /**
     * 租户描述
     */
    private String tenantDesc;

    /**
     * 申请人用户id
     */
    private Long applyUserId;

    /**
     * 申请人名称
     */
    private String applyUserName;

    /**
     * 申请人联系邮箱
     */
    private String applyEmail;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 修改人名称
     */
    private String modifyName;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = DateUtils.DATE_PATTERN.YYYY_MM_DD_HH_MM_SS, timezone = NCConstants.TIME_ZONE.SHANGHAI)
    private Date modifyTime;

    /**
     * 状态 -1-拒绝，0-待审核，1-通过
     */
    private Integer state;

    /**
     * 申请时间
     */
    @JsonFormat(pattern = DateUtils.DATE_PATTERN.YYYY_MM_DD_HH_MM_SS, timezone = NCConstants.TIME_ZONE.SHANGHAI)
    private Date createTime;
}