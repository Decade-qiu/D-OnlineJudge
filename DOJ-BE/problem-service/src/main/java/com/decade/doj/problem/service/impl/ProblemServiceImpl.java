package com.decade.doj.problem.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.decade.doj.common.domain.PageDTO;
import com.decade.doj.common.domain.PageQueryDTO;
import com.decade.doj.problem.domain.dto.ProblemPageQueryDTO;
import com.decade.doj.problem.domain.po.Problem;
import com.decade.doj.problem.mapper.ProblemMapper;
import com.decade.doj.problem.service.IProblemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author
 * @since 2024-09-17
 */
@Service
@Slf4j
public class ProblemServiceImpl extends ServiceImpl<ProblemMapper, Problem> implements IProblemService {

    @Override
    public PageDTO<Problem> pageQuery(ProblemPageQueryDTO problemPageQueryDTO) {
        log.info("分页查询题目列表: {}", problemPageQueryDTO);
        log.info("{} {} {}", problemPageQueryDTO.getName(), problemPageQueryDTO.getDifficulty(), problemPageQueryDTO.getTags());
        Page<Problem> problemList = lambdaQuery()
                .like(problemPageQueryDTO.getName() != null, Problem::getName, problemPageQueryDTO.getName())
                .eq(problemPageQueryDTO.getDifficulty() != null, Problem::getDifficulty, problemPageQueryDTO.getDifficulty())
                .or(problemPageQueryDTO.getTags() != null, wrapper -> {
                    List<String> tags = List.of(problemPageQueryDTO.getTags().split(","));
                    for (String tag : tags) {
                        wrapper.like(Problem::getTag, tag);
                    }
                })
                .page(problemPageQueryDTO.toMpPage("id", true));


        return PageDTO.fullPage(problemList.getTotal(), problemList.getPages(), problemList.getRecords());

    }

    @Override
    public void updateProblemStats(Long problemId, boolean isAccepted) {
        lambdaUpdate()
            .eq(Problem::getId, problemId)
            .setSql("total_attempt = total_attempt + 1")
            .setSql(isAccepted, "total_pass = total_pass + 1")
            .update();
    }

}
