package com.decade.doj.submission.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.decade.doj.submission.domain.po.Submission;
import com.decade.doj.submission.service.ISubmissionService;
import com.decade.doj.submission.mapper.SubmissionMapper;
import org.springframework.stereotype.Service;

/**
* @author qzj
* @description 针对表【submission(代码提交记录表)】的数据库操作Service实现
* @createDate 2025-06-05 13:28:52
*/
@Service
public class ISubmissionServiceImpl extends ServiceImpl<SubmissionMapper, Submission>
    implements ISubmissionService {

}




