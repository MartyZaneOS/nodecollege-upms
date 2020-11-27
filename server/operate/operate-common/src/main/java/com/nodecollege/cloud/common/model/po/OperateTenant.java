package com.nodecollege.cloud.common.model.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.utils.DateUtils;
import lombok.Data;

import java.util.Date;

/**
 * Table: upms_tenant
 * 版权：节点学院
 *
 * @author LC
 * @date 2019-12-06 23:56:14
 */
@Data
public class OperateTenant {
    /**
     * 主键
     */
    private Long tenantId;

    /**
     * 租户名称
     */
    private String tenantName;

    /**
     * 租户代码
     */
    private String tenantCode;

    /**
     * 描述
     */
    private String introduce;

    /**
     * 创建用户id
     */
    private Long createUserId;

    /**
     * 状态 -1-已删除，0-不可删除，1-正常, 2-冻结，3-待审核
     */
    private Integer state;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = DateUtils.DATE_PATTERN.YYYY_MM_DD_HH_MM_SS, timezone = NCConstants.TIME_ZONE.SHANGHAI)
    private Date createTime;
}