package com.nodecollege.cloud.service.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.po.SyncJob;
import com.nodecollege.cloud.common.model.po.SyncJobLog;
import com.nodecollege.cloud.common.model.po.SysVisitLog;
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
@SyncJobAnnotation(jobName = "定时统计访问量", cronExpression = "0 10 0 * * ?", jobParam = "{\"visitDay\": \"\"}",
        description = "visitDay为空时，统计前一天的数据，不为空时，只统计visitDay设置的数据！ 格式yyyyMMdd")
public class CountVisitJob extends QuartzJobBean {

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

        // 获取统计日期
        Integer visitDay = jobParam.getInteger("visitDay");
        SysVisitLog visitLog = new SysVisitLog();
        visitLog.setVisitDay(visitDay);
        NCResult<Map<String, Integer>> result = logClient.countVisitData(visitLog);
        if (result.getSuccess()) {
            Map<String, Integer> res = result.getRows().get(0);
            syncJobLogService.saveLog(SyncJobLog.info(syncJob, "统计" + res.get("visitDay") + "号访问量，添加统计数据" + res.get("addSize") + "条，删除失效数据" + res.get("delSize") + "条！", startTime));
        } else {
            syncJobLogService.saveLog(SyncJobLog.error(syncJob, "统计" + visitDay + "号访问量失败！", result.getMessage(), startTime));
        }
        log.info("定时统计访问量结束！耗时：{}", System.currentTimeMillis() - startTime);
    }
}