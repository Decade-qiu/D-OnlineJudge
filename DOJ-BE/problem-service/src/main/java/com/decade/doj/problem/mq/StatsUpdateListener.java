package com.decade.doj.problem.mq;

import com.decade.doj.problem.service.IProblemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatsUpdateListener {

    private final IProblemService problemService;

    @RabbitListener(queues = "problem.stats.update.queue")
    public void listenStatsUpdateQueue(Map<String, Object> message) {
        if (message == null) {
            return;
        }
        Long problemId = (Long) message.get("problemId");
        Boolean isAccepted = (Boolean) message.get("isAccepted");

        if (problemId == null || isAccepted == null) {
            log.error("接收到无效的消息：{}", message);
            return;
        }
        
        log.info("接收到题目提交消息，开始更新题目统计数据。problemId: {}, isAccepted: {}", problemId, isAccepted);
        problemService.updateProblemStats(problemId, isAccepted);
    }
}
