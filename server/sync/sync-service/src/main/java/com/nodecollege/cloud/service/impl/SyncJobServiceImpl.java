package com.nodecollege.cloud.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.nodecollege.cloud.client.utils.NCLoginUtils;
import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.NCLoginUserVO;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.SyncJob;
import com.nodecollege.cloud.common.model.po.SyncJobLog;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.core.annotation.SyncJobAnnotation;
import com.nodecollege.cloud.dao.mapper.SyncJobMapper;
import com.nodecollege.cloud.service.SyncJobLogService;
import com.nodecollege.cloud.service.SyncJobService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @author LC
 * @date 2020/6/10 19:16
 */
@Slf4j
@Service
public class SyncJobServiceImpl implements SyncJobService {

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private SyncJobMapper syncJobMapper;

    @Autowired
    private SyncJobLogService syncJobLogService;

    @Autowired
    private NCLoginUtils loginUtils;

    private Map<String, Class> classMap = new HashMap<>();

    @PostConstruct
    public void init() {
        // 查询数据库中所有任务
        List<SyncJob> jobList = syncJobMapper.selectList(null);
        Map<String, SyncJob> jobMap = new HashMap<>();
        Map<String, SyncJob> jobNameMap = new HashMap<>();
        for (int i = 0; i < jobList.size(); i++) {
            if (StringUtils.isNotBlank(jobList.get(i).getJobName()) && jobNameMap.containsKey(jobList.get(i).getJobName())) {
                throw new NCException("-1", "数据库中任务名称重复！");
            }
            jobNameMap.put(jobList.get(i).getJobName(), jobList.get(i));
            jobMap.put(jobList.get(i).getJobClass(), jobList.get(i));
        }

        // 查询所有任务执行class
        Reflections reflections = new Reflections("com.nodecollege.cloud");
        Set<Class<? extends QuartzJobBean>> annotated = reflections.getSubTypesOf(QuartzJobBean.class);

        // 遍历任务执行class
        for (Class<?> clazz : annotated) {
            String jobClass = clazz.getName();
            classMap.put(jobClass, clazz);
            if (!jobMap.containsKey(jobClass)) {
                long startTime = System.currentTimeMillis();
                // 任务执行class在库中无记录 新建任务
                SyncJob job = new SyncJob();
                job.setJobName(jobClass);
                job.setJobGroup("DEFAULT_GROUP");
                job.setJobClass(jobClass);
                job.setMisfirePolicy(0);
                job.setCreateTime(new Date());
                job.setJobStatus(0);
                // 获取自定义任务注解
                SyncJobAnnotation an = AnnotationUtils.findAnnotation(clazz, SyncJobAnnotation.class);
                if (an != null) {
                    job.setJobName(an.jobName());
                    job.setJobGroup(an.jobGroup());
                    job.setDescription(an.description());
                    job.setMisfirePolicy(an.misfirePolicy());
                    job.setJobStatus(an.jobStatus());
                    job.setJobParam(an.jobParam());
                    if (CronExpression.isValidExpression(an.cronExpression())) {
                        job.setCronExpression(an.cronExpression());
                    } else {
                        job.setJobStatus(0);
                    }
                    if (StringUtils.isNotBlank(job.getJobName()) && jobNameMap.containsKey(job.getJobName())) {
                        throw new NCException("-1", "任务名称重复！");
                    }
                }
                job.setJobType(0);
                syncJobMapper.insertSelective(job);
                syncJobLogService.saveLog(SyncJobLog.info(job, "任务初始化完成！", startTime));
                jobMap.put(jobClass, job);
                jobList.add(job);
            }
        }
        try {
            // 先清除所有任务
            scheduler.clear();
            for (SyncJob job : jobList) {
                if (classMap.containsKey(job.getJobClass())
                        && StringUtils.isNotBlank(job.getCronExpression())
                        && CronExpression.isValidExpression(job.getCronExpression())
                        && StringUtils.isNotBlank(job.getJobName())) {
                    createScheduleJob(job);
                    continue;
                }
                log.error("任务无法执行,任务id：{},任务名称：{}, 详细：{}", job.getJobId(), job.getJobName(), JSONObject.toJSONString(job));
                if (job.getJobStatus() == 1) {
                    SyncJob update = new SyncJob();
                    update.setJobId(job.getJobId());
                    update.setJobStatus(0);
                    syncJobMapper.updateByPrimaryKeySelective(update);
                }
            }
        } catch (SchedulerException e) {
            log.error("同步服务发生异常", e);
            throw new NCException("-1", "同步服务发生异常");
        }
    }

    /**
     * 创建任务
     */
    private void createScheduleJob(SyncJob syncJob) {
        long startTime = System.currentTimeMillis();
        if (!CronExpression.isValidExpression(syncJob.getCronExpression())) {
            throw new NCException("", "cron表达格式不正确！");   //表达式格式不正确
        }

        //构建job信息
        JobKey jobKey = getJobKey(syncJob);
        JobDetail jobDetail = JobBuilder
                .newJob(classMap.get(syncJob.getJobClass()))
                .withIdentity(jobKey)
                .build();

        //传递参数
        jobDetail.getJobDataMap().put("syncJob", JSONObject.toJSONString(syncJob));

        //表达式调度构建器
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(syncJob.getCronExpression());
        if (syncJob.getMisfirePolicy() == 1) {
            // 立即触发执行
            scheduleBuilder = scheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires();
        } else if (syncJob.getMisfirePolicy() == 2) {
            // 触发一次执行
            scheduleBuilder = scheduleBuilder.withMisfireHandlingInstructionFireAndProceed();
        } else if (syncJob.getMisfirePolicy() == 3) {
            // 不触发立即执行
            scheduleBuilder = scheduleBuilder.withMisfireHandlingInstructionDoNothing();
        }

        //按新的cronExpression表达式构建一个新的trigger
        CronTrigger trigger = TriggerBuilder
                .newTrigger()
                .withIdentity(syncJob.getJobName() + syncJob.getJobClass(), syncJob.getJobGroup())
                .withSchedule(scheduleBuilder)
                .build();

        try {
            // 防止创建时存在数据问题 先移除，然后在执行创建操作
            if (scheduler.checkExists(jobKey)) scheduler.deleteJob(jobKey);
            scheduler.scheduleJob(jobDetail, trigger);
            syncJobLogService.saveLog(SyncJobLog.info(syncJob, "任务启动！", startTime));
            // 暂停任务
            if (syncJob.getJobStatus() == 0) {
                scheduler.pauseJob(jobKey);
                syncJobLogService.saveLog(SyncJobLog.info(syncJob, "任务暂停！", startTime));
            }
        } catch (SchedulerException e) {
            log.error("创建调度任务失败！", e);
            syncJobLogService.saveLog(SyncJobLog.error(syncJob, "任务启动失败！", e.getMessage(), startTime));
            throw new NCException("-1", "创建调度任务失败");
        }
    }

    /**
     * 查询任务列表
     */
    @Override
    public List<SyncJob> getJobList(QueryVO<SyncJob> queryVO) {
        return syncJobMapper.selectListByMap(queryVO.toMap());
    }

    /**
     * 查询所有任务执行class
     */
    @Override
    public Set<String> getJobClassList() {
        return classMap.keySet();
    }

    /**
     * 新增任务
     */
    @Override
    public void addJob(SyncJob syncJob) {
        NCUtils.nullOrEmptyThrow(syncJob.getJobName(), "-1", "任务名称必填！");
        NCUtils.nullOrEmptyThrow(syncJob.getJobGroup(), "-1", "任务组必填！");
        NCUtils.nullOrEmptyThrow(syncJob.getJobClass(), "-1", "任务执行class必填！");
        NCUtils.nullOrEmptyThrow(syncJob.getCronExpression(), "-1", "cron必填！");
        NCUtils.nullOrEmptyThrow(syncJob.getMisfirePolicy(), "-1", "计划策略必填！");
        NCUtils.nullOrEmptyThrow(syncJob.getJobStatus(), "-1", "任务状态必填！");

        SyncJob query = new SyncJob();
        query.setJobName(syncJob.getJobName());
        List<SyncJob> exList = syncJobMapper.selectList(query);
        if (!exList.isEmpty()) {
            throw new NCException("-1", "任务名称已存在！");
        }
        if (!CronExpression.isValidExpression(syncJob.getCronExpression())) {
            throw new NCException("-1", "Illegal cron expression！");
        }
        if (!classMap.containsKey(syncJob.getJobClass())) {
            throw new NCException("-1", "任务执行类不存在！");
        }
        syncJob.setCreateTime(new Date());
        syncJob.setJobType(1);
        NCLoginUserVO loginUserVO = loginUtils.getAdminLoginInfo();
        if (loginUserVO != null) {
            syncJob.setUpdateUser(loginUserVO.getAccount());
        }
        syncJobMapper.insertSelective(syncJob);
        createScheduleJob(syncJob);
    }

    /**
     * 修改任务
     */
    @Override
    public void editJob(SyncJob job) {
        long startTime = System.currentTimeMillis();
        NCUtils.nullOrEmptyThrow(job.getJobId());
        NCUtils.nullOrEmptyThrow(job.getCronExpression());
        SyncJob ex = syncJobMapper.selectByPrimaryKey(job.getJobId());
        NCUtils.nullOrEmptyThrow(ex, "", "该任务不存在！");
        if (!CronExpression.isValidExpression(job.getCronExpression())) {
            throw new NCException("-1", "Illegal cron expression");
        }
        job.setUpdateTime(new Date());
        job.setJobName(null);
        job.setJobGroup(null);
        job.setJobClass(null);
        job.setJobType(null);
        job.setCreateTime(null);
        NCLoginUserVO loginUserVO = loginUtils.getAdminLoginInfo();
        if (loginUserVO != null) {
            job.setUpdateUser(loginUserVO.getAccount());
        }

        syncJobMapper.updateByPrimaryKeySelective(job);
        job = syncJobMapper.selectByPrimaryKey(job.getJobId());
        try {
            scheduler.deleteJob(getJobKey(ex));
        } catch (SchedulerException e) {
            log.error("修改任务失败！", e);
            throw new NCException("-1", "修改任务失败！");
        }
        syncJobLogService.saveLog(SyncJobLog.info(job, "修改任务！", startTime));
        createScheduleJob(job);
    }

    /**
     * 删除某个任务
     */
    @Override
    public void delJob(SyncJob job) {
        long startTime = System.currentTimeMillis();
        NCUtils.nullOrEmptyThrow(job.getJobId());
        job = syncJobMapper.selectByPrimaryKey(job.getJobId());
        if (job.getJobType() == 0) {
            throw new NCException("-1", "原始任务不能删除！");
        }
        syncJobMapper.deleteByPrimaryKey(job.getJobId());
        try {
            scheduler.deleteJob(getJobKey(job));
        } catch (SchedulerException e) {
            log.error("删除任务失败！", e);
            throw new NCException("-1", "删除任务失败！");
        }
        syncJobLogService.saveLog(SyncJobLog.info(job, "任务删除！", startTime));
    }

    /**
     * 暂停/恢复任务
     */
    @Override
    public void pauseJob(SyncJob syncJob) {
        long startTime = System.currentTimeMillis();
        NCUtils.nullOrEmptyThrow(syncJob.getJobId());
        NCUtils.nullOrEmptyThrow(syncJob.getJobStatus());

        SyncJob ex = syncJobMapper.selectByPrimaryKey(syncJob.getJobId());
        NCUtils.nullOrEmptyThrow(ex, "", "任务不存在！");

        if (ex.getCronExpression() == null || !CronExpression.isValidExpression(ex.getCronExpression())) {
            throw new NCException("", "cron表达式为空或不正确，不能操作！");
        }
        try {
            JobKey jobKey = getJobKey(ex);
            if (scheduler.getJobDetail(jobKey) == null) {
                // 任务无法执行
                syncJob.setJobStatus(2);
            } else if (syncJob.getJobStatus() == 0) {
                scheduler.pauseJob(jobKey);
            } else {
                scheduler.resumeJob(jobKey);
            }
        } catch (NCException e) {
            throw new NCException(e.getCode(), e.getMessage());
        } catch (SchedulerException e) {
            log.error("暂停/恢复任务失败！", e);
            throw new NCException("-1", "暂停/恢复任务失败！");
        }
        SyncJob update = new SyncJob();
        update.setJobId(syncJob.getJobId());
        update.setJobStatus(syncJob.getJobStatus());
        update.setUpdateTime(new Date());
        NCLoginUserVO loginUserVO = loginUtils.getAdminLoginInfo();
        if (loginUserVO != null) {
            update.setUpdateUser(loginUserVO.getAccount());
        }
        syncJobMapper.updateByPrimaryKeySelective(update);
        if (syncJob.getJobStatus() == 0) {
            syncJobLogService.saveLog(SyncJobLog.info(ex, "任务暂停！", startTime));
        } else if (syncJob.getJobStatus() == 1) {
            syncJobLogService.saveLog(SyncJobLog.info(ex, "任务启动！", startTime));
        } else {
            syncJobLogService.saveLog(SyncJobLog.info(ex, "任务无法执行！", startTime));
        }
    }

    private JobKey getJobKey(SyncJob syncJob) {
        if (syncJob == null) {
            throw new NCException("-1", "任务为空！");
        }
        return JobKey.jobKey(syncJob.getJobName() + syncJob.getJobClass(), syncJob.getJobGroup());
    }

    private TriggerKey getTriggerKey(SyncJob syncJob) {
        if (syncJob == null) {
            throw new NCException("-1", "任务为空！");
        }
        return TriggerKey.triggerKey(syncJob.getJobName() + syncJob.getJobClass(), syncJob.getJobGroup());
    }
}
