package com.decade.doj.user.service.impl;

import com.decade.doj.common.config.properties.JwtProperties;
import com.decade.doj.common.domain.R;
import com.decade.doj.common.exception.BadRequestException;
import com.decade.doj.common.exception.ForbiddenException;
import com.decade.doj.common.config.custom.JwtTool;
import com.decade.doj.common.utils.UserContext;
import com.decade.doj.user.domain.dto.LoginDTO;
import com.decade.doj.user.domain.dto.RegisterDTO;
import com.decade.doj.user.domain.dto.UpdPwdDTO;
import com.decade.doj.user.domain.vo.LoginVO;
import com.decade.doj.user.mapper.UserMapper;
import com.decade.doj.user.domain.po.User;
import com.decade.doj.user.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.decade.doj.user.utils.AESTool;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 
 * @since 2024-08-26
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final AESTool aesTool;
    private final JwtTool jwtTool;
    private final JwtProperties jwtProperties;

    @Override
    public R<LoginVO> login(LoginDTO loginDTO) {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        User user = lambdaQuery().eq(User::getUsername, username).one();
        if (user == null) {
            throw new BadRequestException("用户名错误");
        }
        if (!aesTool.match(password, user.getPassword())) {
            throw new BadRequestException("密码错误");
        }
        if (user.getBan()) {
            throw new ForbiddenException("用户被冻结");
        }
        // token
        String token = jwtTool.createToken(user.getId(), jwtProperties.getTokenTTL());

        LoginVO loginVO = new LoginVO()
                .setToken(token)
                .setUserId(user.getId())
                .setUsername(user.getUsername());
        return R.ok(loginVO);
    }

    @Override
    public R register(RegisterDTO registerDTO) {
        String username = registerDTO.getUsername();
        String password = registerDTO.getPassword();
        String email = registerDTO.getEmail();
        String signature = registerDTO.getSign();
        boolean exist = lambdaQuery().eq(User::getUsername, username).exists();
        if (exist) {
            throw new BadRequestException("用户名已存在");
        }
        User user = new User()
                .setUsername(username)
                .setPassword(aesTool.encode(password, aesTool.fnv1aHash(password)))
                .setEmail(email)
                .setSign(signature);
        save(user);
        return R.ok();
    }

    @Override
    public R updatePwd(UpdPwdDTO updPwdDTO) {
        String oldPassword = updPwdDTO.getOldPassword();
        String newPassword = updPwdDTO.getNewPassword();
        User user = getById(UserContext.getCurrentUser());
        if (user == null) {
            return R.error("用户不存在!");
        }
        if (!aesTool.match(oldPassword, user.getPassword())) {
            return R.error("原密码错误!");
        }
        user.setPassword(aesTool.encode(newPassword, aesTool.fnv1aHash(newPassword)));
        updateById(user);
        return R.ok();
    }

    @Override
    public R updateUser(User user) {
        user.setId(UserContext.getCurrentUser());
        User col = lambdaQuery().eq(User::getUsername, user.getUsername()).one();
        if (col != null && !col.getId().equals(user.getId())) {
            return R.error("用户名已存在!");
        }
        user.setBan(null)
                .setPassword(null)
                .setScore(null)
                .setRanks(null)
                .setEasySolve(null)
                .setMiddleSolve(null)
                .setHardSolve(null)
                .setRole(null)
                .setFans(null)
                .setSubscribe(null);
        updateById(user);
        return R.ok();
    }
}
