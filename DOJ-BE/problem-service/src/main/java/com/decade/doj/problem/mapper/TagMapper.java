package com.decade.doj.problem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.decade.doj.problem.domain.po.Tag;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TagMapper extends BaseMapper<Tag> {
}
