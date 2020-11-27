package com.nodecollege.cloud.service.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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

import java.util.HashMap;
import java.util.Map;

@Slf4j
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Component
@SyncJobAnnotation(jobName = "定时删除系统日志", cronExpression = "0 0 1 * * ?", jobParam = "{\"retentionDays\": 30}")
public class DeleteSysLogJob extends QuartzJobBean {

    @Autowired
    private SyncJobLogService syncJobLogService;

    @Autowired
    private LogClient logClient;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        long startTime = System.currentTimeMillis();
        JobDataMap map = context.getJobDetail().getJobDataMap();
        SyncJob syncJob = JSON.parseObject(map.getString("syncJob"), SyncJob.class);
        JSONObject jobParam = JSONObject.parseObject(syncJob.getJobParam());

        // 获取保留天数
        Integer retentionDays = jobParam.getInteger("retentionDays");
        Map<String, Integer> param = new HashMap<>();
        param.put("retentionDays", retentionDays);
        NCResult<Map<String, Integer>> result = logClient.delSysLog(param);

        if (result.getSuccess()) {
            syncJobLogService.saveLog(SyncJobLog.info(syncJob, "保留最近" + retentionDays + "天日志，删除过期日志" + result.getRows().get(0).get("delSize") + "条！", startTime));
        } else {
            syncJobLogService.saveLog(SyncJobLog.error(syncJob, "保留最近" + retentionDays + "天日志，删除过期日志失败！", result.getMessage(), startTime));
        }

        log.info("定时删除系统日志结束！耗时：{}", System.currentTimeMillis() - startTime);
    }
}