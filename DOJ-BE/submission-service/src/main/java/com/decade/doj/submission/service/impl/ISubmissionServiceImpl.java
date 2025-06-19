package com.decade.doj.submission.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.decade.doj.common.domain.PageDTO;
import com.decade.doj.submission.domain.dto.SubmissionPageQueryDTO;
import com.decade.doj.submission.domain.po.Submission;
import com.decade.doj.submission.service.ISubmissionService;
import com.decade.doj.submission.mapper.SubmissionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author qzj
* @description 针对表【submission(代码提交记录表)】的数据库操作Service实现
* @createDate 2025-06-05 13:28:52
*/
@Service
@Slf4j
public class ISubmissionServiceImpl extends ServiceImpl<SubmissionMapper, Submission>
    implements ISubmissionService {

    @Override
    public PageDTO<Submission> pageQuery(SubmissionPageQueryDTO submissionPageQueryDTO) {
        log.info("分页查询提交列表: {}", submissionPageQueryDTO);
        log.info("userId={}, problemId={}, status={}, language={}",
                submissionPageQueryDTO.getUserId(),
                submissionPageQueryDTO.getProblemId(),
                submissionPageQueryDTO.getStatus(),
                submissionPageQueryDTO.getLanguage());

        Page<Submission> submissionList = lambdaQuery()
                .eq(submissionPageQueryDTO.getUserId() != null && !submissionPageQueryDTO.getUserId().isBlank(), Submission::getUserId, submissionPageQueryDTO.getUserId())
                .eq(submissionPageQueryDTO.getProblemId() != null, Submission::getProblemId, submissionPageQueryDTO.getProblemId())
                .eq(submissionPageQueryDTO.getStatus() != null, Submission::getStatus, submissionPageQueryDTO.getStatus())
                .eq(submissionPageQueryDTO.getLanguage() != null, Submission::getLanguage, submissionPageQueryDTO.getLanguage())
                .page(submissionPageQueryDTO.toMpPage("id", true));

        return PageDTO.fullPage(submissionList.getTotal(), submissionList.getPages(), submissionList.getRecords());
    }

}




