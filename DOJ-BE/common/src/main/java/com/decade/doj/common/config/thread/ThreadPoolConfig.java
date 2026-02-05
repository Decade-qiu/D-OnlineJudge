package com.decade.doj.common.config.thread;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置类
 * <p>
 * 支持通过 application.yml 配置参数：
 * <pre>
 * doj:
 *   thread-pool:
 *     run-code-core-size: 10
 *     run-code-max-size: 20
 *     run-code-queue-capacity: 50
 *     judging-core-size: 4
 *     judging-max-size: 8
 *     judging-queue-capacity: 100
 * </pre>
 */
@Configuration
@EnableAsync
@ConfigurationProperties(prefix = "doj.thread-pool")
@Data
@Slf4j
public class ThreadPoolConfig {

    // RunCodeThreadPool 配置
    private int runCodeCoreSize = 10;
    private int runCodeMaxSize = 20;
    private int runCodeQueueCapacity = 50;

    // JudgingThreadPool 配置
    private int judgingCoreSize = 4;
    private int judgingMaxSize = 8;
    private int judgingQueueCapacity = 100;

    /**
     * 代码运行线程池
     * 用于执行 /code 和 /problem 端点的代码运行任务
     */
    @Bean("RunCodeThreadPool")
    public ThreadPoolTaskExecutor runCodeThreadPoolExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(runCodeCoreSize);
        executor.setMaxPoolSize(runCodeMaxSize);
        executor.setQueueCapacity(runCodeQueueCapacity);
        executor.setThreadNamePrefix("RunCodeThread-");
        // 拒绝策略：调用者线程执行，防止任务丢失
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 优雅关闭：等待任务完成
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        log.info("RunCodeThreadPool initialized: coreSize={}, maxSize={}, queueCapacity={}",
                runCodeCoreSize, runCodeMaxSize, runCodeQueueCapacity);
        return executor;
    }

    /**
     * 判题任务线程池
     * 用于执行从 Redis 队列消费的判题任务
     */
    @Bean("JudgingThreadPool")
    public ThreadPoolTaskExecutor judgingThreadPoolExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(judgingCoreSize);
        executor.setMaxPoolSize(judgingMaxSize);
        executor.setQueueCapacity(judgingQueueCapacity);
        executor.setThreadNamePrefix("JudgingThread-");
        // 拒绝策略：调用者线程执行，确保任务不丢失
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 优雅关闭：判题任务可能较长，给予更多等待时间
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(120);
        executor.initialize();
        log.info("JudgingThreadPool initialized: coreSize={}, maxSize={}, queueCapacity={}",
                judgingCoreSize, judgingMaxSize, judgingQueueCapacity);
        return executor;
    }
}

