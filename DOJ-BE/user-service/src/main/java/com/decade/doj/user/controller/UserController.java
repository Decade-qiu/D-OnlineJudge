package com.decade.doj.user.controller;


import com.decade.doj.common.domain.R;
import com.decade.doj.user.domain.dto.LoginDTO;
import com.decade.doj.user.domain.dto.RegisterDTO;
import com.decade.doj.user.domain.po.User;
import com.decade.doj.user.domain.vo.LoginVO;
import com.decade.doj.user.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 
 * @since 2024-08-26
 */
@RestController
@RequestMapping("/user")
@Api(tags = "用户相关接口")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @PostMapping("/register")
    @ApiOperation("用户登录接口")
    public R register(@RequestBody @Validated RegisterDTO registerDTO) {
        return userService.register(registerDTO);
    }

    @PostMapping("/login")
    @ApiOperation("用户登录接口")
    public R<LoginVO> login(@RequestBody @Validated LoginDTO loginDTO) {
        return userService.login(loginDTO);
    }

    @GetMapping("/get/{id}")
    @ApiOperation("查询用户接口")
    public User getUser(@PathVariable("id") Long id) {
        log.info("查询用户：{}", id);
        return userService.getById(id);
    }

}
