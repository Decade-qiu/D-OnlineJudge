package com.decade.doj.user.mapper;

import com.decade.doj.user.domain.po.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 
 * @since 2024-08-26
 */
public interface UserMapper extends BaseMapper<User> {

    @Select("select id, username, sign, easy_solve from user where id = #{id}")
    User chooseById(Integer id);

}
