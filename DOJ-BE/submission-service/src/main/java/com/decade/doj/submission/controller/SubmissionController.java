package com.decade.doj.submission.controller;

import com.decade.doj.common.domain.PageDTO;
import com.decade.doj.common.domain.R;
import com.decade.doj.common.domain.vo.ExecuteMessage;
import com.decade.doj.common.utils.UserContext;
import com.decade.doj.submission.domain.dto.SubmissionPageQueryDTO;
import com.decade.doj.submission.domain.po.Submission;
import com.decade.doj.submission.service.ISubmissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/submission")
@Api(tags = "提交相关接口")
@Slf4j
@RequiredArgsConstructor
public class SubmissionController {

    private final ISubmissionService submissionService;

    @PostMapping("/submit")
    @ApiOperation("提交记录")
    public R<Long> submit(@RequestBody Submission submission) {
        submissionService.save(submission);
        return R.ok(submission.getId());
    }

    @GetMapping("/page")
    @ApiOperation("分页获取提交列表")
    public R<PageDTO<Submission>> page(SubmissionPageQueryDTO problemPageQueryDTO) {
        PageDTO<Submission> res = submissionService.pageQuery(problemPageQueryDTO);
        return R.ok(res);
    }

    @GetMapping("/match/{id}")
    @ApiOperation("获取当前用户指定问题的提交详情")
    public R<Integer> getById(@PathVariable String id) {
        List<Submission> submissions = submissionService.lambdaQuery()
                .eq(Submission::getProblemId, id)
                .list();
        int f = 0;
        for (Submission submission : submissions) {
            if (submission.getUserId() != null && submission.getUserId().equals(UserContext.getCurrentUser())) {
                f = 1;
                if (ExecuteMessage.getStatus(submission.getExitValue()).equals("Accepted")) {
                    return R.ok(1); // 已经提交过且通过
                }
            }
        }
        if (f == 1) {
            return R.ok(2); // 已经提交过但未通过
        }
        return R.ok(0);
    }
}
