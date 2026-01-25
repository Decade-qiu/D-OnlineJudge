package com.decade.doj.problem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.decade.doj.problem.domain.dto.ProblemTagView;
import com.decade.doj.problem.domain.po.ProblemTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProblemTagMapper extends BaseMapper<ProblemTag> {

    @Select({
            "<script>",
            "SELECT pt.problem_id AS problemId, t.name AS tagName",
            "FROM problem_tag pt",
            "JOIN tag t ON pt.tag_id = t.id",
            "WHERE pt.problem_id IN",
            "<foreach collection='problemIds' item='id' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"
    })
    List<ProblemTagView> selectTagsByProblemIds(@Param("problemIds") List<Long> problemIds);
}
