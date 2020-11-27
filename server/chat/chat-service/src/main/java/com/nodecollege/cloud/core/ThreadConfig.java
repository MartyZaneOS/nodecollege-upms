package com.nodecollege.cloud.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author LC
 * @date 20:49 2020/3/17
 **/
@Configuration
@EnableAsync
public class ThreadConfig {
    /**
     * 配置线程池
     *
     * @return
     */
    @Bean(name = "asyncPoolTaskExecutor")
    public ThreadPoolTaskExecutor getAsyncThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        // 初始大小
        taskExecutor.setCorePoolSize(5);
        // 线程池维护线程的最大数量
        taskExecutor.setMaxPoolSize(10);
        // 缓存队列
        taskExecutor.setQueueCapacity(30);
        // 允许的空闲时间
        taskExecutor.setKeepAliveSeconds(200);
        taskExecutor.setThreadNamePrefix("event-");
        // 线程池对拒绝任务（无线程可用）的处理策略，目前只支持AbortPolicy、CallerRunsPolicy；默认为后者
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.initialize();
        return taskExecutor;
    }
}
