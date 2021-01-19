package com.nodecollege.cloud.common.model.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.utils.DateUtils;
import lombok.Data;

import java.util.Date;

/**
 * Table: sys_visit_log
 * 版权：节点学院
 *
 * @author LC
 * @date 2020-11-30 11:25:31
 */
@Data
public class SysVisitLog {
    // id
    private Long id;

    // 访问类型 0-微服务接口访问量，1-微服务访问量, 2-ip接口访问量，3-ip访问量，4-文章访问量
    private Integer visitType;

    // 时间维度 0-分钟，1-小时，2-天
    private Integer timeDimension;

    // 访问天 yyyyMMdd
    private Integer visitDay;

    // 访问小时 HH
    private String visitHour;

    // 访问分钟 HHmm
    private String visitMinute;

    // 访问微服务名称
    private String visitAppName;

    // 访问地址
    private String visitUrl;

    // 访问ip
    private String visitIp;

    // 访问总数
    private Long visitCount;

    // 访问ip数
    private Long visitIpCount;

    // 创建时间
    @JsonFormat(pattern = DateUtils.DATE_PATTERN.YYYY_MM_DD_HH_MM_SS, timezone = NCConstants.TIME_ZONE.SHANGHAI)
    private Date createTime;
}