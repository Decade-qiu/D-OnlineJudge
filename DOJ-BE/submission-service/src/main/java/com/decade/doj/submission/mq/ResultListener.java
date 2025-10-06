package com.decade.doj.submission.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.decade.doj.common.client.ProblemClient;
import com.decade.doj.common.client.UserClient;
import com.decade.doj.common.domain.po.Problem;
import com.decade.doj.common.domain.po.User;
import com.decade.doj.common.utils.UserContext;
import com.decade.doj.submission.domain.po.Submission;
import com.decade.doj.submission.service.ISubmissionService;
import com.decade.doj.submission.websocket.SubmissionWSServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResultListener {

    private final ISubmissionService submissionService;
    private final ProblemClient problemClient;
    private final UserClient userClient;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "judging.result.queue")
    public void onMessage(Map<String, Object> message) {
        log.info("从 RabbitMQ 收到判题结果消息: {}", message);
        try {
            Long submissionId = Long.valueOf(message.get("submissionId").toString());
            Map<String, Object> executeMessage = (Map<String, Object>) message.get("executeMessage");

            if (executeMessage == null) {
                log.error("消息格式错误，缺少 submissionId 或 executeMessage");
                return;
            }

            // 1. 更新数据库
            Submission submission = submissionService.getById(submissionId);
            UserContext.setCurrentUser(submission.getUserId());
            User user = userClient.getUser(submission.getUserId()).getData();
            if (user == null) {
                log.error("用户 {} 不存在，无法更新提交记录", submission.getUserId());
                return;
            }
            submission.setUserName(user.getUsername());
            Problem problem = problemClient.getProblemById(submission.getProblemId()).getData();
            if (problem == null) {
                log.error("题目 {} 不存在，无法更新提交记录", submission.getProblemId());
                return;
            }
            submission.setProblemName(problem.getName());
            submission.setId(submissionId);
            submission.setStatus((String) executeMessage.get("status"));
            submission.setExitValue((Integer) executeMessage.get("exitValue"));
            submission.setMessage((String) executeMessage.get("message"));
            submission.setTime((Double) executeMessage.get("time"));
            submission.setMemory(((Number) executeMessage.get("memory")).longValue());
            submissionService.updateById(submission);
            log.info("提交记录 {} 已更新", submissionId);

            // 2. 通过 WebSocket 推送给前端
            SubmissionWSServer.sendMessage(submissionId, JSON.toJSONString(submission));

            // 3. 发送 RabbitMQ 事件，用于更新用户和题目统计
            Map<String, Object> submissionMessage = Map.of(
                "problemId", submission.getProblemId(),
                "isAccepted", "Accepted".equals(submission.getStatus())
            );
            rabbitTemplate.convertAndSend("doj.topic", "submission.created", submissionMessage);

            if ("Accepted".equals(submission.getStatus())) {
                long acCount = submissionService.lambdaQuery()
                    .eq(Submission::getUserId, submission.getUserId())
                    .eq(Submission::getProblemId, submission.getProblemId())
                    .eq(Submission::getStatus, "Accepted")
                    .count();
                if (acCount == 1) {
                    Map<String, Long> solvedMessage = Map.of(
                        "userId", submission.getUserId(),
                        "problemId", submission.getProblemId()
                    );
                    rabbitTemplate.convertAndSend("doj.topic", "problem.solved", solvedMessage);
                    log.info("用户 {} 首次 AC 题目 {}，已发送 problem.solved 事件", submission.getUserId(), submission.getProblemId());
                }
            }

        } catch (Exception e) {
            log.error("处理判题结果消息时发生异常", e);
        }
    }
}
