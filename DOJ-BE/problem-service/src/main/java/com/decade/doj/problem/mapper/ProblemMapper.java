package com.decade.doj.problem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.decade.doj.problem.domain.po.Problem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 
 * @since 2024-09-17
 */
public interface ProblemMapper extends BaseMapper<Problem> {

    IPage<Problem> selectPageWithFilters(Page<Problem> page,
                                         @Param("ids") List<Long> ids,
                                         @Param("difficulty") String difficulty,
                                         @Param("tagNames") List<String> tagNames,
                                         @Param("status") String status,
                                         @Param("userId") Long userId);

}
