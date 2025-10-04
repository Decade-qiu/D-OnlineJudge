package com.decade.doj.user.service;

import com.decade.doj.common.domain.R;
import com.decade.doj.user.domain.dto.LoginDTO;
import com.decade.doj.user.domain.dto.RegisterDTO;
import com.decade.doj.user.domain.dto.UpdPwdDTO;
import com.decade.doj.user.domain.po.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.decade.doj.user.domain.vo.LoginVO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 
 * @since 2024-08-26
 */
public interface IUserService extends IService<User> {

    R<LoginVO> login(LoginDTO loginDTO);

    R<String> refreshToken(String refreshToken);

    R register(RegisterDTO registerDTO);

    R updateUser(User user);

    R updatePwd(UpdPwdDTO updPwdDTO);
}
