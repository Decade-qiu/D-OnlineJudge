package com.decade.doj.submission.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.decade.doj.common.domain.PageDTO;
import com.decade.doj.submission.domain.dto.SubmissionPageQueryDTO;
import com.decade.doj.submission.domain.po.Submission;
import com.decade.doj.submission.service.ISubmissionService;
import com.decade.doj.submission.mapper.SubmissionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
* @author qzj
* @description 针对表【submission(代码提交记录表)】的数据库操作Service实现
* @createDate 2025-06-05 13:28:52
*/
@Service
@Slf4j
@RequiredArgsConstructor
public class ISubmissionServiceImpl extends ServiceImpl<SubmissionMapper, Submission>
    implements ISubmissionService {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public boolean save(Submission submission) {
        boolean saved = super.save(submission);
        if (!saved) {
            return false;
        }

        // 无论提交是否成功，都发送一个“提交创建”事件，用于更新题目的总尝试次数
        Map<String, Object> submissionMessage = Map.of(
            "problemId", submission.getProblemId(),
            "isAccepted", "Accepted".equals(submission.getStatus())
        );
        rabbitTemplate.convertAndSend("doj.topic", "submission.created", submissionMessage);

        // 如果不是AC，则直接返回
        if (!"Accepted".equals(submission.getStatus())) {
            return true;
        }

        // 查询在这条记录之前，是否已经有过 AC 记录
        long previousAcCount = this.lambdaQuery()
                .eq(Submission::getUserId, submission.getUserId())
                .eq(Submission::getProblemId, submission.getProblemId())
                .eq(Submission::getStatus, "Accepted")
                .count();

        // 如果之前的 AC 记录数等于 1 (也就是刚刚插入的这条)，说明是首次 AC
        if (previousAcCount == 1) {
            log.info("用户 {} 首次 AC 题目 {}，发送消息到MQ", submission.getUserId(), submission.getProblemId());
            // 发送消息到 MQ
            Map<String, Long> message = Map.of(
                "userId", submission.getUserId(),
                "problemId", submission.getProblemId()
            );
            rabbitTemplate.convertAndSend("doj.topic", "problem.solved", message);
        }

        return true;
    }

    @Override
    public PageDTO<Submission> pageQuery(SubmissionPageQueryDTO submissionPageQueryDTO) {
        log.info("分页查询提交列表: {}", submissionPageQueryDTO);
        log.info("userId={}, problemId={}, status={}, language={}",
                submissionPageQueryDTO.getUserId(),
                submissionPageQueryDTO.getProblemId(),
                submissionPageQueryDTO.getStatus(),
                submissionPageQueryDTO.getLanguage());

        String user = submissionPageQueryDTO.getUserId();
        String problem = submissionPageQueryDTO.getProblemId();

        Page<Submission> submissionList = lambdaQuery()
                .like(user != null && !user.isBlank(), Submission::getUserName, submissionPageQueryDTO.getUserId())
                .like(problem != null && !problem.isBlank(), Submission::getProblemName, submissionPageQueryDTO.getProblemId())
                .eq(submissionPageQueryDTO.getStatus() != null, Submission::getStatus, submissionPageQueryDTO.getStatus())
                .eq(submissionPageQueryDTO.getLanguage() != null, Submission::getLanguage, submissionPageQueryDTO.getLanguage())
                .page(submissionPageQueryDTO.toMpPage("id", true));

        return PageDTO.fullPage(submissionList.getTotal(), submissionList.getPages(), submissionList.getRecords());
    }

}




