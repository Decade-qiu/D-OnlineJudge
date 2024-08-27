package com.decade.doj.user.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LoginVO {

    private String token;
    private Long userId;
    private String username;

}