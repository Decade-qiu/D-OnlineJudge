package com.decade.doj.user.controller;


import cn.hutool.core.bean.BeanUtil;
import com.decade.doj.common.config.properties.ResourceProperties;
import com.decade.doj.common.domain.PageQueryDTO;
import com.decade.doj.common.domain.PageDTO;
import com.decade.doj.common.domain.R;
import com.decade.doj.user.domain.dto.LoginDTO;
import com.decade.doj.user.domain.dto.RegisterDTO;
import com.decade.doj.user.domain.dto.UpdPwdDTO;
import com.decade.doj.user.domain.po.User;
import com.decade.doj.user.domain.vo.InfoVO;
import com.decade.doj.user.domain.vo.LoginVO;
import com.decade.doj.user.domain.vo.RankVO;
import com.decade.doj.user.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

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
@Tag(name = "用户相关接口")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    private final ResourceProperties resourceProperties;

    @PutMapping("/pwd")
    @Operation(summary = "修改密码接口")
    public R updatePwd(@RequestBody @Validated UpdPwdDTO updPwdDTO) {
        return userService.updatePwd(updPwdDTO);
    }

    @PostMapping("/avatar")
    @Operation(summary = "上传头像接口")
    public R<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return R.error("文件为空!");
        }
        String filename = UUID.randomUUID() + file.getOriginalFilename();
        Path path = Paths.get(resourceProperties.getLocation() + filename);

        try {
            file.transferTo(path);
        } catch (Exception e) {
            return R.error("文件上传失败!");
        }

        return R.ok(resourceProperties.getRequest()+filename);
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册接口")
    public R register(@RequestBody @Validated RegisterDTO registerDTO) {
        return userService.register(registerDTO);
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录接口")
    public R<LoginVO> login(@RequestBody @Validated LoginDTO loginDTO) {
        return userService.login(loginDTO);
    }

    @PostMapping("/refresh")
    @Operation(summary = "刷新令牌接口")
    public R<String> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        return userService.refreshToken(refreshToken);
    }

    @GetMapping("/rankings")
    @Operation(summary = "获取排行榜")
    public R<PageDTO<RankVO>> getRankings(PageQueryDTO pageQueryDTO) {
        return userService.getRankings(pageQueryDTO);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询用户接口")
    public R<InfoVO> getUser(@PathVariable("id") @NotNull Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return R.error("用户不存在!");
        }
        InfoVO infoVO = BeanUtil.copyProperties(user, InfoVO.class);
        return R.ok(infoVO);
    }

    @PutMapping()
    @Operation(summary = "修改用户接口")
    public R updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

}
