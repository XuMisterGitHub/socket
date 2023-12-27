package com.bingo.socket.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;

@Configuration
public class TreadExecutorConfig {

    @Value("${theadPool.corePoolSize}")
    private int corePoolSize;

    @Value("${theadPool.maximumPoolSize}")
    private int maximumPoolSize;

    @Value("${theadPool.keepAliveTime}")
    private int keepAliveTime;

    @Value("${theadPool.capacity}")
    private int capacity;

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maximumPoolSize);
        // 线程池维护线程所允许的空闲时间
        executor.setKeepAliveSeconds(keepAliveTime);
        // 队列长度
        executor.setQueueCapacity(capacity);
        // 等待任务执行完成在关闭
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //该方法用来设置线程池中任务的等待时间，如果超过这个时候还没有销毁就强制销毁，以确保应用最后能够被关闭，而不是阻塞住。
        executor.setAwaitTerminationSeconds(60);
        // 线程前缀名称
        executor.setThreadNamePrefix("async-user-");
        // 配置拒绝策略：如果队列满了，继续往队列增加数据，则直接丢弃
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        executor.initialize();
        return executor;
    }
}