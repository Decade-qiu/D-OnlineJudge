package com.decade.doj.user.service;

import com.decade.doj.common.domain.PageDTO;
import com.decade.doj.common.domain.PageQueryDTO;
import com.decade.doj.common.domain.R;
import com.decade.doj.common.domain.vo.StatsVO;
import com.decade.doj.user.domain.dto.LoginDTO;
import com.decade.doj.user.domain.dto.RegisterDTO;
import com.decade.doj.user.domain.dto.UpdPwdDTO;
import com.decade.doj.user.domain.po.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.decade.doj.user.domain.vo.LoginVO;
import com.decade.doj.user.domain.vo.RankVO;

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

    R<PageDTO<RankVO>> getRankings(PageQueryDTO pageQueryDTO);

    void handleProblemSolved(Long userId, Long problemId);

    StatsVO getStats();
}
