package com.decade.doj.problem.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.decade.doj.common.domain.PageDTO;
import com.decade.doj.problem.domain.dto.ProblemPageQueryDTO;
import com.decade.doj.problem.domain.po.Problem;
import com.decade.doj.problem.mapper.ProblemMapper;
import com.decade.doj.problem.service.IProblemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service
@Slf4j
public class ProblemServiceImpl extends ServiceImpl<ProblemMapper, Problem> implements IProblemService {

    private final CacheManager redisCacheManager;
    private final CacheManager caffeineCacheManager;
    private final RabbitTemplate rabbitTemplate;

    public ProblemServiceImpl(@Qualifier("redisCacheManager") CacheManager redisCacheManager,
                              @Qualifier("caffeineCacheManager") CacheManager caffeineCacheManager,
                              RabbitTemplate rabbitTemplate) {
        this.redisCacheManager = redisCacheManager;
        this.caffeineCacheManager = caffeineCacheManager;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public Problem getById(Serializable id) {
        // 1. 查 L1 本地缓存
        Cache caffeineCache = caffeineCacheManager.getCache("problem");
        if (caffeineCache != null && caffeineCache.get(id) != null) {
            log.info("L1 缓存命中, id: {}", id);
            return caffeineCache.get(id, Problem.class);
        }

        // 2. 查 L2 Redis 缓存
        Cache redisCache = redisCacheManager.getCache("problem");
        if (redisCache != null && redisCache.get(id) != null) {
            log.info("L2 缓存命中, id: {}", id);
            Problem problem = redisCache.get(id, Problem.class);
            // 将数据写回 L1
            if (caffeineCache != null && problem != null) {
                caffeineCache.put(id, problem);
            }
            return problem;
        }

        // 3. 查 L3 数据库
        log.info("缓存未命中，从数据库查询, id: {}", id);
        Problem problem = super.getById(id);

        // 4. 依次写回 L2 和 L1
        if (problem != null) {
            if (redisCache != null) {
                redisCache.put(id, problem);
            }
            if (caffeineCache != null) {
                caffeineCache.put(id, problem);
            }
        }
        return problem;
    }

    @Override
    public boolean updateById(Problem entity) {
        // 1. 更新数据库
        boolean success = super.updateById(entity);
        if (success) {
            // 2. 清理缓存
            clearCache(entity.getId());
        }
        return success;
    }

    @Override
    public boolean removeById(Serializable id) {
        // 1. 删除数据库
        boolean success = super.removeById(id);
        if (success) {
            // 2. 清理缓存
            clearCache(id);
        }
        return success;
    }

    private void clearCache(Serializable id) {
        // 1. 删除 L2 Redis 缓存
        Cache redisCache = redisCacheManager.getCache("problem");
        if (redisCache != null) {
            redisCache.evict(id);
            log.info("L2 缓存已清除, id: {}", id);
        }
        // 2. 发送 MQ 消息，通知其他实例删除 L1 缓存
        rabbitTemplate.convertAndSend("cache.update.exchange", "", id.toString());
        log.info("发送缓存更新广播, id: {}", id);
    }

    // 监听 MQ 消息，删除本地 L1 缓存
    @RabbitListener(queues = "#{@problemCacheUpdateQueue.name}")
    public void onCacheUpdate(String id) {
        Cache caffeineCache = caffeineCacheManager.getCache("problem");
        if (caffeineCache != null) {
            caffeineCache.evict(Long.valueOf(id));
            log.info("收到广播，L1 缓存已清除, id: {}", id);
        }
    }

    @Override
    public PageDTO<Problem> pageQuery(ProblemPageQueryDTO problemPageQueryDTO) {
        log.info("分页查询题目列表: {}", problemPageQueryDTO);
        Page<Problem> problemList = lambdaQuery()
                .like(problemPageQueryDTO.getName() != null, Problem::getName, problemPageQueryDTO.getName())
                .eq(problemPageQueryDTO.getDifficulty() != null, Problem::getDifficulty, problemPageQueryDTO.getDifficulty())
                .or(problemPageQueryDTO.getTags() != null, wrapper -> {
                    List<String> tags = List.of(problemPageQueryDTO.getTags().split(","));
                    for (String tag : tags) {
                        wrapper.like(Problem::getTag, tag);
                    }
                })
                .page(problemPageQueryDTO.toMpPage("id", true));

        return PageDTO.fullPage(problemList.getTotal(), problemList.getPages(), problemList.getRecords());
    }

    @Override
    public void updateProblemStats(Long problemId, boolean isAccepted) {
        lambdaUpdate()
            .eq(Problem::getId, problemId)
            .setSql("total_attempt = total_attempt + 1")
            .setSql(isAccepted, "total_pass = total_pass + 1")
            .update();
    }
}