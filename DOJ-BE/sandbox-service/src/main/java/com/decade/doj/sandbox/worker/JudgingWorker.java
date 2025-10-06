package com.decade.doj.sandbox.worker;

import com.alibaba.fastjson.JSON;
import com.decade.doj.sandbox.domain.vo.JudgingTask;
import com.decade.doj.sandbox.service.ISandboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class JudgingWorker implements ApplicationRunner {

    private final StringRedisTemplate redisTemplate;
    private final ISandboxService sandboxService;

    @Value("${doj.sandbox.worker-threads:4}")
    private int workerThreads;

    private static final String JUDGING_QUEUE_KEY = "judging:queue";

    @Override
    public void run(ApplicationArguments args) {
        ExecutorService executor = Executors.newFixedThreadPool(workerThreads);

        for (int i = 0; i < workerThreads; i++) {
            executor.submit(() -> {
                log.info("判题 Worker 线程 {} 已启动。", Thread.currentThread().getName());
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        String taskJson = redisTemplate.opsForList()
                                .rightPop(JUDGING_QUEUE_KEY, 30, TimeUnit.SECONDS);
                        if (taskJson != null) {
                            JudgingTask task = JSON.parseObject(taskJson, JudgingTask.class);
                            log.info("Worker 开始处理任务: {}", task);
                            sandboxService.execute(task);
                        }
                    } catch (Exception e) {
                        log.error("判题 Worker 线程出现异常", e);
                    }
                }
            });
        }
        log.info("启动了 {} 个判题 Worker 线程。", workerThreads);
    }
}
