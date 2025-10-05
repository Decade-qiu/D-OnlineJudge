package com.decade.doj.user.mq;

import com.decade.doj.common.utils.UserContext;
import com.decade.doj.user.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatsUpdateListener {

    private final IUserService userService;

    @RabbitListener(queues = "stats.update.queue")
    public void listenStatsUpdateQueue(Map<String, Long> message) {
        if (message == null) {
            return;
        }
        Long userId = message.get("userId");
        Long problemId = message.get("problemId");
        if (userId == null || problemId == null) {
            log.error("接收到无效的消息：{}", message);
            return;
        }
        log.info("接收到用户解题消息，开始更新用户统计数据。userId: {}, problemId: {}", userId, problemId);
        // 调用业务方法处理
        userService.handleProblemSolved(userId, problemId);
    }
}
