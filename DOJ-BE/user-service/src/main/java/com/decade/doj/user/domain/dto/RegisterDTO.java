package com.decade.doj.user.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Accessors(chain = true)
@Schema(description = "注册信息")
public class RegisterDTO {

    @Schema(description = "用户名", required = true)
    @NotBlank(message = "用户名不能为空")
    private String username;

    @Schema(description = "密码", required = true)
    @NotBlank(message = "密码不能为空")
    private String password;

    @Schema(description = "邮箱", required = true)
    @Email(message = "邮箱格式不正确")
    private String email;

    @Schema(description = "签名")
    private String sign;

}
