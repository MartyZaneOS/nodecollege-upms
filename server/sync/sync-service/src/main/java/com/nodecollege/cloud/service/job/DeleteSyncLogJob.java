package com.nodecollege.cloud.service.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nodecollege.cloud.common.model.QueryVO;
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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Component
@SyncJobAnnotation(jobName = "定时删除任务日志", cronExpression = "0 0 1 * * ?", jobParam = "{\"retentionDays\": 30}")
public class DeleteSyncLogJob extends QuartzJobBean {

    @Autowired
    private SyncJobLogService syncJobLogService;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        long startTime = System.currentTimeMillis();
        JobDataMap map = context.getJobDetail().getJobDataMap();
        SyncJob syncJob = JSON.parseObject(map.getString("syncJob"), SyncJob.class);
        JSONObject jobParam = JSONObject.parseObject(syncJob.getJobParam());

        // 获取保留天数
        Integer retentionDays = jobParam.getInteger("retentionDays");

        if (retentionDays == null) {
            // 默认30天
            retentionDays = 30;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -retentionDays);

        QueryVO queryDelete = new QueryVO();
        queryDelete.setEndTime(calendar.getTime());
        List<SyncJobLog> delList = syncJobLogService.getLogList(queryDelete);
        for (SyncJobLog log : delList) {
            syncJobLogService.delLog(log);
        }

        syncJobLogService.saveLog(SyncJobLog.info(syncJob, "保留最近" + retentionDays + "天日志，删除过期日志" + delList.size() + "条！", startTime));
        log.info("定时删除同步任务日志结束！耗时：{}", System.currentTimeMillis() - startTime);
    }
}