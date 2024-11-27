package com.decade.doj.problem.controller;


import com.decade.doj.common.domain.PageDTO;
import com.decade.doj.common.domain.PageQueryDTO;
import com.decade.doj.common.domain.R;
import com.decade.doj.problem.domain.dto.ProblemPageQueryDTO;
import com.decade.doj.problem.domain.po.Problem;
import com.decade.doj.problem.service.impl.ProblemServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 
 * @since 2024-09-17
 */
@RestController
@RequestMapping("/problem")
@Api(tags = "题目相关接口")
@Slf4j
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemServiceImpl problemService;

    @GetMapping("/list")
    @ApiOperation("获取题目列表")
    public R<List<Problem>> list() {
        return R.ok(problemService.list());
    }

    @GetMapping("/page")
    @ApiOperation("分页获取题目列表")
    public R<PageDTO<Problem>> page(ProblemPageQueryDTO problemPageQueryDTO) {
        PageDTO<Problem> res = problemService.pageQuery(problemPageQueryDTO);
        return R.ok(res);
    }

    @GetMapping("/{id}")
    @ApiOperation("获取题目详情")
    public R<Problem> getProblemById(@PathVariable Long id) {
        return R.ok(problemService.getById(id));
    }

}
