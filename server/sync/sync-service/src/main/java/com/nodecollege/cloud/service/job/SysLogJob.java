package com.nodecollege.cloud.service.job;

import com.alibaba.fastjson.JSON;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.po.SyncJob;
import com.nodecollege.cloud.common.model.po.SyncJobLog;
import com.nodecollege.cloud.core.annotation.SyncJobAnnotation;
import com.nodecollege.cloud.feign.LogClient;
import com.nodecollege.cloud.service.SyncJobLogService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Component
@SyncJobAnnotation(jobName = "系统日志定时入库", cronExpression = "*/30 * * * * ?", jobParam = "")
public class SysLogJob extends QuartzJobBean {

    @Autowired
    private SyncJobLogService syncJobLogService;

    @Autowired
    private LogClient logClient;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        long startTime = System.currentTimeMillis();
        JobDataMap map = context.getJobDetail().getJobDataMap();
        NCResult<Map<String, Integer>> result = logClient.sysLogStorage();
        SyncJob syncJob = JSON.parseObject(map.getString("syncJob"), SyncJob.class);
        if (result.getSuccess()) {
            syncJobLogService.saveLog(SyncJobLog.info(syncJob, "系统日志入库" + result.getRows().get(0).get("addSize") + "条！", startTime));
        } else {
            syncJobLogService.saveLog(SyncJobLog.error(syncJob, "系统日志定时入库失败！", result.getMessage(), startTime));
        }
        log.info("系统日志定时入库任务结束！耗时：{}", System.currentTimeMillis() - startTime);
    }
}