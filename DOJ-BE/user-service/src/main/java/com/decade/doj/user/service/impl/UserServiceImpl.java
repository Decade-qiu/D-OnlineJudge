package com.decade.doj.user.service.impl;

import com.decade.doj.common.client.ProblemClient;
import com.decade.doj.common.config.properties.JwtProperties;
import com.decade.doj.common.domain.PageDTO;
import com.decade.doj.common.domain.PageQueryDTO;
import com.decade.doj.common.domain.R;
import com.decade.doj.common.domain.po.Problem;
import com.decade.doj.common.exception.BadRequestException;
import com.decade.doj.common.exception.ForbiddenException;
import com.decade.doj.common.config.custom.JwtTool;
import com.decade.doj.common.exception.UnauthorizedException;
import com.decade.doj.common.utils.UserContext;
import com.decade.doj.user.domain.dto.LoginDTO;
import com.decade.doj.user.domain.dto.RegisterDTO;
import com.decade.doj.user.domain.dto.UpdPwdDTO;
import com.decade.doj.user.domain.vo.LoginVO;
import com.decade.doj.user.domain.vo.RankVO;
import com.decade.doj.user.mapper.UserMapper;
import com.decade.doj.user.domain.po.User;
import com.decade.doj.user.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.decade.doj.common.config.properties.AppNameProperties;
import com.decade.doj.user.utils.AESTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.Random;

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
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final AESTool aesTool;
    private final JwtTool jwtTool;
    private final JwtProperties jwtProperties;
    private final StringRedisTemplate redisTemplate;
    private final AppNameProperties appNameProperties;
    private final ProblemClient problemClient;

    @PostConstruct
    public void initRankings() {
        // 服务启动时，将所有用户数据同步到 Redis 排行榜
        List<User> users = this.list();
        if (users.isEmpty()) {
            return;
        }
        Set<ZSetOperations.TypedTuple<String>> tuples = users.stream()
                .map(user -> ZSetOperations.TypedTuple.of(user.getId().toString(), user.getScore().doubleValue()))
                .collect(Collectors.toSet());
        redisTemplate.opsForZSet().add(getRankingKey(), tuples);
        log.info("用户排行榜数据已同步到 Redis。");
    }

    private String getRedisKeyPrefix() {
        return appNameProperties.getName() + ":refresh_token:";
    }

    private String getRankingKey() {
        return appNameProperties.getName() + ":ranks";
    }

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
        // 1. 生成访问令牌 (Access Token)
        String accessToken = jwtTool.createToken(user.getId(), jwtProperties.getTokenTTL());

        // 2. 生成刷新令牌 (Refresh Token)
        String refreshToken = UUID.randomUUID().toString();

        // 3. 将 Refresh Token 存入 Redis
        String redisKey = getRedisKeyPrefix() + refreshToken;
        redisTemplate.opsForValue().set(redisKey, user.getId().toString(), jwtProperties.getRefreshTokenTTL());

        // 4. 封装并返回双令牌
        LoginVO loginVO = new LoginVO()
                .setAccessToken(accessToken)
                .setRefreshToken(refreshToken)
                .setUserId(user.getId())
                .setUsername(user.getUsername());
        return R.ok(loginVO);
    }

    @Override
    public R<String> refreshToken(String refreshToken) {
        String redisKey = getRedisKeyPrefix() + refreshToken;
        String userIdStr = redisTemplate.opsForValue().get(redisKey);

        if (userIdStr == null) {
            throw new UnauthorizedException("无效的刷新令牌");
        }

        Long userId = Long.valueOf(userIdStr);
        String accessToken = jwtTool.createToken(userId, jwtProperties.getTokenTTL());
        return R.ok(accessToken);
    }

    @Override
    public R<PageDTO<RankVO>> getRankings(PageQueryDTO pageQueryDTO) {
        long start = (long) (pageQueryDTO.getPageNo() - 1) * pageQueryDTO.getPageSize();
        long end = start + pageQueryDTO.getPageSize() - 1;

        // 1. 从 Redis 的 Sorted Set 中获取排名和分数
        Set<ZSetOperations.TypedTuple<String>> tuples = redisTemplate.opsForZSet().reverseRangeWithScores(getRankingKey(), start, end);
        if (tuples == null || tuples.isEmpty()) {
            return R.ok(PageDTO.empty(0L, 0L));
        }

        // 2. 提取用户ID并批量查询用户信息
        List<Long> userIds = tuples.stream().map(tuple -> Long.valueOf(Objects.requireNonNull(tuple.getValue()))).toList();
        Map<Long, User> userMap = this.listByIds(userIds).stream().collect(Collectors.toMap(User::getId, user -> user));

        // 3. 组装VO
        List<RankVO> rankVOs = new ArrayList<>();
        long currentRank = start + 1;
        List<String> languages = List.of("cpp", "java", "python");
        Random random = new Random();

        for (ZSetOperations.TypedTuple<String> tuple : tuples) {
            Long userId = Long.valueOf(Objects.requireNonNull(tuple.getValue()));
            User user = userMap.get(userId);
            if (user != null) {
                RankVO rankVO = new RankVO();
                rankVO.setRank(currentRank++);
                rankVO.setUserId(user.getId());
                rankVO.setUsername(user.getUsername());
                rankVO.setAvatar(user.getAvatar());
                rankVO.setScore(user.getScore());
                rankVO.setEasySolve(user.getEasySolve());
                rankVO.setMiddleSolve(user.getMiddleSolve());
                rankVO.setHardSolve(user.getHardSolve());
                // 随机设置常用语言用于前端展示
                rankVO.setMostUsedLanguage(languages.get(random.nextInt(languages.size())));
                rankVOs.add(rankVO);
            }
        }

        Long total = redisTemplate.opsForZSet().zCard(getRankingKey());
        long pages = (total + pageQueryDTO.getPageSize() - 1) / pageQueryDTO.getPageSize();

        return R.ok(PageDTO.fullPage(total, pages, rankVOs));
    }

    @Override
    public void handleProblemSolved(Long userId, Long problemId) {
        UserContext.setCurrentUser(userId);
        Problem problem = problemClient.getProblemById(problemId).getData();
        if (problem == null) return;

        User user = this.getById(userId);
        if (user == null) return;

        int scoreChange = switch (problem.getDifficulty()) {
            case "简单" -> {
                user.setEasySolve(user.getEasySolve() + 1);
                yield 500;
            }
            case "中等" -> {
                user.setMiddleSolve(user.getMiddleSolve() + 1);
                yield 1000;
            }
            case "困难" -> {
                user.setHardSolve(user.getHardSolve() + 1);
                yield 2000;
            }
            default -> 0;
        };

        user.setScore(user.getScore() + scoreChange);
        this.updateById(user);
        redisTemplate.opsForZSet().add(getRankingKey(), user.getId().toString(), user.getScore());
        log.info("用户 {} 分数及统计数据更新成功", userId);
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
        // 将新用户同步到排行榜
        redisTemplate.opsForZSet().add(getRankingKey(), user.getId().toString(), 0.0);
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
