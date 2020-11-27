package com.nodecollege.cloud.service.job;

import com.alibaba.fastjson.JSON;
import com.nodecollege.cloud.common.model.po.SyncJob;
import com.nodecollege.cloud.common.model.po.SyncJobLog;
import com.nodecollege.cloud.core.annotation.SyncJobAnnotation;
import com.nodecollege.cloud.service.SyncJobLogService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Component
@SyncJobAnnotation(jobName = "第一个job", cronExpression = "0/15 * * * * ?", jobParam = "{}")
public class HelloWorldJob extends QuartzJobBean {

    @Autowired
    private SyncJobLogService syncJobLogService;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        long startTime = System.currentTimeMillis();
        JobDataMap map = context.getJobDetail().getJobDataMap();
        int sum = 0;
        if (map.containsKey("sum")) {
            sum = map.getInt("sum");
        }
        sum++;
        map.put("sum", sum);
        log.info("自定义参数 sum {}", sum);
        SyncJob syncJob = new SyncJob();
        if (map.containsKey("syncJob")) {
            syncJob = JSON.parseObject(map.getString("syncJob"), SyncJob.class);
        }
        syncJobLogService.saveLog(SyncJobLog.info(syncJob, "自定义参数：" + sum, startTime));

        log.info("任务名称： {}，任务执行类 {}，任务执行cron {},时间 {}", syncJob.getJobName(), syncJob.getJobClass(),
                syncJob.getCronExpression(),
                (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")).format(new Date()));
    }
}