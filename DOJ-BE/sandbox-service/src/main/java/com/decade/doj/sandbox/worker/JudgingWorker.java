package com.decade.doj.sandbox.worker;

import com.alibaba.fastjson.JSON;
import com.decade.doj.sandbox.domain.vo.JudgingTask;
import com.decade.doj.sandbox.service.ISandboxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 判题任务消费者
 * <p>
 * 使用单一调度线程从 Redis 队列轮询任务，并将任务分发到 JudgingThreadPool 执行。
 * 实现 DisposableBean 接口支持优雅关闭。
 */
@Slf4j
@Component
public class JudgingWorker implements ApplicationRunner, DisposableBean {

    private final StringRedisTemplate redisTemplate;
    private final ISandboxService sandboxService;
    private final ThreadPoolTaskExecutor judgingExecutor;

    private static final String JUDGING_QUEUE_KEY = "judging:queue";
    private static final int POLL_TIMEOUT_SECONDS = 5;

    private final AtomicBoolean running = new AtomicBoolean(true);
    private Thread schedulerThread;

    public JudgingWorker(
            StringRedisTemplate redisTemplate,
            ISandboxService sandboxService,
            @Qualifier("JudgingThreadPool") ThreadPoolTaskExecutor judgingExecutor
    ) {
        this.redisTemplate = redisTemplate;
        this.sandboxService = sandboxService;
        this.judgingExecutor = judgingExecutor;
    }

    @Override
    public void run(ApplicationArguments args) {
        schedulerThread = new Thread(this::pollAndDispatch, "JudgingScheduler");
        schedulerThread.start();
        log.info("判题调度器已启动，任务将分发到 JudgingThreadPool 执行");
    }

    /**
     * 轮询 Redis 队列并分发任务到线程池
     */
    private void pollAndDispatch() {
        while (running.get()) {
            try {
                String taskJson = redisTemplate.opsForList()
                        .rightPop(JUDGING_QUEUE_KEY, POLL_TIMEOUT_SECONDS, TimeUnit.SECONDS);

                if (taskJson != null) {
                    JudgingTask task = JSON.parseObject(taskJson, JudgingTask.class);
                    log.info("收到判题任务: submissionId={}, problemId={}", 
                            task.getSubmissionId(), task.getProblemId());

                    // 分发到线程池异步执行
                    judgingExecutor.execute(() -> executeTask(task));
                }
            } catch (Exception e) {
                if (running.get()) {
                    log.error("轮询判题队列出现异常", e);
                    // 短暂休眠避免错误风暴
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
        log.info("判题调度器已停止轮询");
    }

    /**
     * 执行单个判题任务
     */
    private void executeTask(JudgingTask task) {
        try {
            log.debug("开始执行判题任务: submissionId={}", task.getSubmissionId());
            sandboxService.execute(task);
            log.debug("判题任务执行完成: submissionId={}", task.getSubmissionId());
        } catch (Exception e) {
            log.error("执行判题任务异常: submissionId={}, problemId={}", 
                    task.getSubmissionId(), task.getProblemId(), e);
        }
    }

    @Override
    public void destroy() {
        log.info("正在关闭判题调度器...");
        running.set(false);

        if (schedulerThread != null) {
            schedulerThread.interrupt();
            try {
                // 等待调度线程终止
                schedulerThread.join(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        log.info("判题调度器已关闭，线程池将由 Spring 容器管理关闭");
    }
}

