package com.nodecollege.cloud.common.model.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.utils.DateUtils;
import lombok.Data;

import java.util.Date;

/**
 * Table: sync_job_log
 * 版权：节点学院
 *
 * @author LC
 * @date 2020-06-12 17:13:24
 */
@Data
public class SyncJobLog {
    // 任务日志主键
    private Long jobLogId;

    // 任务主键
    private Long jobId;

    // 任务名称
    private String jobName;

    // 任务组
    private String jobGroup;

    // 日志消息
    private String logMessage;

    // 日志状态，0-正常，1-异常
    private Integer logState;

    // 错误日志信息
    private String errorMessage;

    // 耗时
    private Long lostTime;

    // 创建时间
    @JsonFormat(pattern = DateUtils.DATE_PATTERN.YYYY_MM_DD_HH_MM_SS, timezone = NCConstants.TIME_ZONE.SHANGHAI)
    private Date createTime;

    public SyncJobLog(){}

    public SyncJobLog(SyncJob syncJob, String message, Integer state, String errorMessage, long startTime) {
        this.jobId = syncJob.getJobId();
        this.jobName = syncJob.getJobName();
        this.jobGroup = syncJob.getJobGroup();
        this.logMessage = message;
        this.logState = state;
        this.errorMessage = errorMessage;
        this.lostTime = System.currentTimeMillis() - startTime;
    }

    public static SyncJobLog info(SyncJob syncJob, String message, long startTime) {
        return new SyncJobLog(syncJob, message, 0, "", startTime);
    }

    public static SyncJobLog error(SyncJob syncJob, String message, String errorMessage, long startTime) {
        return new SyncJobLog(syncJob, message, 1, errorMessage, startTime);
    }
}