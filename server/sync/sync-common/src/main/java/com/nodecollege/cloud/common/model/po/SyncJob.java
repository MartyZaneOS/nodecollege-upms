package com.nodecollege.cloud.common.model.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.utils.DateUtils;
import lombok.Data;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;

/**
 * Table: sync_job
 * 版权：节点学院
 *
 * @author LC
 * @date 2020-06-10 15:42:30
 */
@Data
public class SyncJob {
    // 任务ID
    private Long jobId;

    // 任务名称
    private String jobName;

    // 任务组名
    private String jobGroup;

    // 任务描述
    private String description;

    // 任务类
    private String jobClass;

    // 任务参数
    private String jobParam;

    // cron执行表达式
    private String cronExpression;

    // 任务状态 0-暂停,1-正常,2-无法执行
    private Integer jobStatus;

    // 计划策略 0-默认,1-立即触发执行,2-触发一次执行,3-不触发立即执行
    private Integer misfirePolicy;

    // 创建时间
    @JsonFormat(pattern = DateUtils.DATE_PATTERN.YYYY_MM_DD_HH_MM_SS, timezone = NCConstants.TIME_ZONE.SHANGHAI)
    private Date createTime;

    // 更新用户
    private String updateUser;

    // 更新时间
    @JsonFormat(pattern = DateUtils.DATE_PATTERN.YYYY_MM_DD_HH_MM_SS, timezone = NCConstants.TIME_ZONE.SHANGHAI)
    private Date updateTime;

    // 任务类型 0-原始任务（不可删除），1-复制任务
    private Integer jobType;
}