package com.decade.doj.problem.service;

import com.decade.doj.common.domain.PageDTO;
import com.decade.doj.common.domain.PageQueryDTO;
import com.decade.doj.problem.domain.dto.ProblemPageQueryDTO;
import com.decade.doj.problem.domain.po.Problem;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 
 * @since 2024-09-17
 */
public interface IProblemService extends IService<Problem> {

    PageDTO<Problem> pageQuery(ProblemPageQueryDTO problemPageQueryDTO);

    void updateProblemStats(Long problemId, boolean isAccepted);

    int syncAllToElasticsearch();

    int reindexAll();

    void resetProblems();
}
