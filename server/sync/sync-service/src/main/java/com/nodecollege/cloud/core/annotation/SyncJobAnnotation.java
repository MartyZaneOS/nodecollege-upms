package com.nodecollege.cloud.core.annotation;

import java.lang.annotation.*;

/**
 * 定时任务注解
 *
 * @author LC
 * @date 15:50 2020/6/11
 **/
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SyncJobAnnotation {
    /**
     * 模块名称
     */
    String jobName();

    /**
     * 任务组名
     */
    String jobGroup() default "DEFAULT_GROUP";

    /**
     * 任务描述
     */
    String description() default "";

    /**
     * cron执行表达式
     */
    String cronExpression();

    /**
     * 任务参数
     */
    String jobParam();

    /**
     * 计划策略 0-默认,1-立即触发执行,2-触发一次执行,3-不触发立即执行
     */
    int misfirePolicy() default 0;

    /**
     * 任务状态 0-暂停,1-正常,2-无法执行
     */
    int jobStatus() default 0;
}
