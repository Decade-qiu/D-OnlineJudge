package com.decade.doj.submission.controller;

import com.decade.doj.common.domain.PageDTO;
import com.decade.doj.common.domain.R;
import com.decade.doj.submission.domain.dto.SubmissionPageQueryDTO;
import com.decade.doj.submission.domain.po.Submission;
import com.decade.doj.submission.service.ISubmissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/submission")
@Api(tags = "提交相关接口")
@Slf4j
@RequiredArgsConstructor
public class SubmissionController {

    private final ISubmissionService submissionService;

    @PostMapping("/submit")
    @ApiOperation("提交记录")
    public R<String> submit(@RequestBody Submission submission) {
        submissionService.save(submission);
        return R.ok("提交成功");
    }

    @GetMapping("/page")
    @ApiOperation("分页获取提交列表")
    public R<PageDTO<Submission>> page(SubmissionPageQueryDTO problemPageQueryDTO) {
        PageDTO<Submission> res = submissionService.pageQuery(problemPageQueryDTO);
        return R.ok(res);
    }

}
