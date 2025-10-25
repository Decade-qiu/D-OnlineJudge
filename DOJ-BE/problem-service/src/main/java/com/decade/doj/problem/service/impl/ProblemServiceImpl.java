package com.decade.doj.problem.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.decade.doj.common.domain.PageDTO;
import com.decade.doj.problem.domain.document.ProblemDocument;
import com.decade.doj.problem.domain.dto.ProblemPageQueryDTO;
import com.decade.doj.problem.domain.po.Problem;
import com.decade.doj.problem.mapper.ProblemMapper;
import com.decade.doj.problem.repository.ProblemRepository;
import com.decade.doj.problem.service.IProblemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProblemServiceImpl extends ServiceImpl<ProblemMapper, Problem> implements IProblemService {

    private final CacheManager redisCacheManager;
    private final CacheManager caffeineCacheManager;
    private final RabbitTemplate rabbitTemplate;
    private final ProblemRepository problemRepository;
    private final ElasticsearchRestTemplate elasticsearchTemplate;

    public ProblemServiceImpl(@Qualifier("redisCacheManager") CacheManager redisCacheManager,
                              @Qualifier("caffeineCacheManager") CacheManager caffeineCacheManager,
                              RabbitTemplate rabbitTemplate,
                              ProblemRepository problemRepository,
                              ElasticsearchRestTemplate elasticsearchTemplate) {
        this.redisCacheManager = redisCacheManager;
        this.caffeineCacheManager = caffeineCacheManager;
        this.rabbitTemplate = rabbitTemplate;
        this.problemRepository = problemRepository;
        this.elasticsearchTemplate = elasticsearchTemplate;
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
    public boolean save(Problem entity) {
        // 1. 保存到数据库
        boolean success = super.save(entity);
        if (success) {
            // 2. 同步到 Elasticsearch
            syncToElasticsearch(entity);
        }
        return success;
    }

    @Override
    public boolean updateById(Problem entity) {
        // 1. 更新数据库
        boolean success = super.updateById(entity);
        if (success) {
            // 2. 清理缓存
            clearCache(entity.getId());
            // 3. 同步到 Elasticsearch
            syncToElasticsearch(entity);
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
            // 3. 从 Elasticsearch 删除
            deleteFromElasticsearch((Long) id);
        }
        return success;
    }

    /**
     * 同步数据到 Elasticsearch
     */
    private void syncToElasticsearch(Problem problem) {
        try {
            ProblemDocument document = new ProblemDocument();
            BeanUtils.copyProperties(problem, document);
            problemRepository.save(document);
            log.info("同步到 ES 成功, id: {}", problem.getId());
        } catch (Exception e) {
            log.error("同步到 ES 失败, id: {}, error: {}", problem.getId(), e.getMessage(), e);
        }
    }

    /**
     * 从 Elasticsearch 删除
     */
    private void deleteFromElasticsearch(Long id) {
        try {
            problemRepository.deleteById(id);
            log.info("从 ES 删除成功, id: {}", id);
        } catch (Exception e) {
            log.error("从 ES 删除失败, id: {}, error: {}", id, e.getMessage(), e);
        }
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

        // 判断是否需要使用 ES 搜索
        boolean useElasticsearch = needElasticsearchSearch(problemPageQueryDTO);

        if (useElasticsearch) {
            // 使用 ES 搜索
            return searchByElasticsearch(problemPageQueryDTO);
        } else {
            // 使用 MySQL 查询
            return searchByDatabase(problemPageQueryDTO);
        }
    }

    /**
     * 判断是否需要使用 ES 搜索
     * 当有文本搜索需求时使用 ES,简单的精确查询使用 MySQL
     */
    private boolean needElasticsearchSearch(ProblemPageQueryDTO dto) {
        return StringUtils.hasText(dto.getName()) || StringUtils.hasText(dto.getTags());
    }

    /**
     * 使用 Elasticsearch 搜索
     */
    private PageDTO<Problem> searchByElasticsearch(ProblemPageQueryDTO dto) {
        log.info("使用 ES 搜索");

        // 构建查询条件
        Criteria criteria = new Criteria();

        // 题目名称模糊搜索 (支持分词搜索)
        if (StringUtils.hasText(dto.getName())) {
            criteria.and(new Criteria("name").matches(dto.getName()));
        }

        // 难度精确匹配
        if (dto.getDifficulty() != null) {
            criteria.and(new Criteria("difficulty").is(dto.getDifficulty()));
        }

        // 标签搜索 (支持多标签)
        if (StringUtils.hasText(dto.getTags())) {
            List<String> tags = List.of(dto.getTags().split(","));
            Criteria tagCriteria = null;
            for (String tag : tags) {
                Criteria c = new Criteria("tag").contains(tag.trim());
                tagCriteria = (tagCriteria == null) ? c : tagCriteria.or(c);
            }
            if (tagCriteria != null) {
                criteria.and(tagCriteria);
            }
        }

        // 构建分页查询
        PageRequest pageRequest = PageRequest.of(
                dto.getPageNo() - 1,
                dto.getPageSize()
        );

        CriteriaQuery query = new CriteriaQuery(criteria).setPageable(pageRequest);

        // 执行搜索
        SearchHits<ProblemDocument> searchHits = elasticsearchTemplate.search(query, ProblemDocument.class);

        // 转换结果
        List<Problem> problems = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .map(this::documentToProblem)
                .collect(Collectors.toList());

        long total = searchHits.getTotalHits();
        long pages = (total + dto.getPageSize() - 1) / dto.getPageSize();

        return PageDTO.fullPage(total, pages, problems);
    }

    /**
     * 使用数据库查询
     */
    private PageDTO<Problem> searchByDatabase(ProblemPageQueryDTO dto) {
        log.info("使用数据库查询");

        Page<Problem> problemList = lambdaQuery()
                .like(dto.getName() != null, Problem::getName, dto.getName())
                .eq(dto.getDifficulty() != null, Problem::getDifficulty, dto.getDifficulty())
                .page(dto.toMpPage("id", true));

        return PageDTO.fullPage(problemList.getTotal(), problemList.getPages(), problemList.getRecords());
    }

    /**
     * ProblemDocument 转 Problem
     */
    private Problem documentToProblem(ProblemDocument document) {
        Problem problem = new Problem();
        BeanUtils.copyProperties(document, problem);
        return problem;
    }

    @Override
    public void updateProblemStats(Long problemId, boolean isAccepted) {
        lambdaUpdate()
                .eq(Problem::getId, problemId)
                .setSql("total_attempt = total_attempt + 1")
                .setSql(isAccepted, "total_pass = total_pass + 1")
                .update();

        // 更新后同步到 ES
        Problem problem = super.getById(problemId);
        if (problem != null) {
            syncToElasticsearch(problem);
        }
    }

    /**
     * 全量同步数据到 ES (用于初始化或数据修复)
     */
    public Integer syncAllToElasticsearch() {
        log.info("开始全量同步数据到 ES");
        List<Problem> allProblems = super.list();

        List<ProblemDocument> documents = allProblems.stream()
                .map(problem -> {
                    ProblemDocument document = new ProblemDocument();
                    BeanUtils.copyProperties(problem, document);
                    log.info("<UNK>: {}", document);
                    return document;
                })
                .collect(Collectors.toList());
        log.info("全量同步完成,共同步 {} 条数据", documents.size());
        problemRepository.saveAll(documents);
        log.info("全量同步完成,共同步 {} 条数据", documents.size());
        return documents.size();
    }
}