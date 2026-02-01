package com.decade.doj.submission.service;

import com.decade.doj.common.domain.PageDTO;
import com.decade.doj.common.domain.vo.SubmissionStatsVO;
import com.decade.doj.submission.domain.dto.SubmissionPageQueryDTO;
import com.decade.doj.submission.domain.po.Submission;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qzj
* @description 针对表【submission(代码提交记录表)】的数据库操作Service
* @createDate 2025-06-05 13:28:52
*/
public interface ISubmissionService extends IService<Submission> {
    PageDTO<Submission> pageQuery(SubmissionPageQueryDTO submissionPageQueryDTO);

    SubmissionStatsVO getStats();
}
