package com.decade.doj.user.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author 
 * @since 2024-08-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user")
public class User {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String username;

    private String avatar = "";

    private String email;

    private String password;

    private Integer score = 0;

    private Integer ranks = 0;

    private String school = "";

    private Boolean gender = true;

    private Integer easySolve = 0;

    private Integer middleSolve = 0;

    private Integer hardSolve = 0;

    private Boolean role = true;

    private String url = "";

    private String sign = "这个人很懒，什么都没留下";

    private Long fans = 0L;

    private Long subscribe = 0L;

    private Boolean ban = false;


}
