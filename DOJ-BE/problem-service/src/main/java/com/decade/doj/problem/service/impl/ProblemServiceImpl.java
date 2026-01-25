package com.decade.doj.problem.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.decade.doj.common.domain.PageDTO;
import com.decade.doj.problem.domain.document.ProblemDocument;
import com.decade.doj.common.utils.UserContext;
import com.decade.doj.problem.domain.dto.ProblemPageQueryDTO;
import com.decade.doj.problem.domain.dto.ProblemTagView;
import com.decade.doj.problem.domain.po.Problem;
import com.decade.doj.problem.domain.po.ProblemTag;
import com.decade.doj.problem.domain.po.Tag;
import com.decade.doj.problem.mapper.ProblemMapper;
import com.decade.doj.problem.mapper.ProblemTagMapper;
import com.decade.doj.problem.mapper.TagMapper;
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
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProblemServiceImpl extends ServiceImpl<ProblemMapper, Problem> implements IProblemService {

    private static final int MAX_ES_RESULTS = 10000;

    private final CacheManager redisCacheManager;
    private final CacheManager caffeineCacheManager;
    private final RabbitTemplate rabbitTemplate;
    private final ProblemRepository problemRepository;
    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final TagMapper tagMapper;
    private final ProblemTagMapper problemTagMapper;

    public ProblemServiceImpl(@Qualifier("redisCacheManager") CacheManager redisCacheManager,
                              @Qualifier("caffeineCacheManager") CacheManager caffeineCacheManager,
                              RabbitTemplate rabbitTemplate,
                              ProblemRepository problemRepository,
                              ElasticsearchRestTemplate elasticsearchTemplate,
                              TagMapper tagMapper,
                              ProblemTagMapper problemTagMapper) {
        this.redisCacheManager = redisCacheManager;
        this.caffeineCacheManager = caffeineCacheManager;
        this.rabbitTemplate = rabbitTemplate;
        this.problemRepository = problemRepository;
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.tagMapper = tagMapper;
        this.problemTagMapper = problemTagMapper;
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
        if (problem != null) {
            attachTags(List.of(problem));
        }

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
            // 2. 处理标签
            syncTags(entity.getId(), entity.getTags());
            // 3. 同步到 Elasticsearch
            syncToElasticsearch(entity);
        }
        return success;
    }

    @Override
    public boolean updateById(Problem entity) {
        // 1. 更新数据库
        boolean success = super.updateById(entity);
        if (success) {
            // 2. 同步标签
            syncTags(entity.getId(), entity.getTags());
            // 3. 清理缓存
            clearCache(entity.getId());
            // 4. 同步到 Elasticsearch
            syncToElasticsearch(entity);
        }
        return success;
    }

    @Override
    public boolean removeById(Serializable id) {
        // 1. 删除数据库
        boolean success = super.removeById(id);
        if (success) {
            // 2. 删除标签关联
            problemTagMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ProblemTag>()
                    .eq(ProblemTag::getProblemId, id));
            // 3. 清理缓存
            clearCache(id);
            // 4. 从 Elasticsearch 删除
            deleteFromElasticsearch((Long) id);
        }
        return success;
    }

    /**
     * 同步标签到数据库
     */
    private void syncTags(Long problemId, List<String> tags) {
        // 1. 先删除原有关联
        problemTagMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ProblemTag>()
                .eq(ProblemTag::getProblemId, problemId));

        if (tags == null || tags.isEmpty()) {
            return;
        }

        Set<String> normalizedTags = new LinkedHashSet<>();
        for (String tagName : tags) {
            if (!StringUtils.hasText(tagName)) {
                continue;
            }
            normalizedTags.add(tagName.trim());
        }

        // 2. 处理每个标签
        for (String tagName : normalizedTags) {
            // 查找标签，不存在则创建
            Tag tag = tagMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Tag>()
                    .eq(Tag::getName, tagName));
            if (tag == null) {
                tag = new Tag().setName(tagName);
                tagMapper.insert(tag);
            }
            // 创建关联
            problemTagMapper.insert(new ProblemTag(problemId, tag.getId()));
        }
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

        List<Long> ids = null;
        if (needElasticsearchSearch(problemPageQueryDTO)) {
            ids = searchIdsByElasticsearch(problemPageQueryDTO);
            if (ids.isEmpty()) {
                long esCount = safeEsCount();
                long dbCount = super.count();
                if (shouldResyncEs(esCount, dbCount)) {
                    syncAllToElasticsearch();
                    ids = searchIdsByElasticsearch(problemPageQueryDTO);
                }
            }
            if (ids.isEmpty()) {
                return PageDTO.empty(0L, 0L);
            }
        }

        List<String> tagNames = normalizeTags(problemPageQueryDTO.getTags());

        Page<Problem> page = (Page<Problem>) baseMapper.selectPageWithFilters(
                problemPageQueryDTO.toMpPage("id", true),
                ids,
                problemPageQueryDTO.getDifficulty(),
                tagNames,
                problemPageQueryDTO.getStatus(),
                UserContext.getCurrentUser()
        );
        List<Problem> records = page.getRecords();
        attachTags(records);
        return PageDTO.fullPage(page.getTotal(), page.getPages(), records);
    }

    /**
     * 判断是否需要使用 ES 搜索
     * 当有文本搜索需求时使用 ES,简单的精确查询使用 MySQL
     */
    private boolean needElasticsearchSearch(ProblemPageQueryDTO dto) {
        return StringUtils.hasText(dto.getName()) || StringUtils.hasText(dto.getDescription());
    }

    /**
     * 使用 Elasticsearch 搜索
     */
    private List<Long> searchIdsByElasticsearch(ProblemPageQueryDTO dto) {
        log.info("使用 ES 搜索并结合 MySQL 过滤");

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolean hasQuery = false;
        if (StringUtils.hasText(dto.getName())) {
            boolQuery.should(QueryBuilders.matchQuery("name", dto.getName()).fuzziness(Fuzziness.AUTO));
            boolQuery.should(QueryBuilders.matchQuery("description", dto.getName()).fuzziness(Fuzziness.AUTO));
            hasQuery = true;
        }
        if (StringUtils.hasText(dto.getDescription())) {
            boolQuery.should(QueryBuilders.matchQuery("description", dto.getDescription()).fuzziness(Fuzziness.AUTO));
            hasQuery = true;
        }

        if (!hasQuery) {
            return List.of();
        }

        boolQuery.minimumShouldMatch(1);
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withPageable(PageRequest.of(0, MAX_ES_RESULTS))
                .build();
        SearchHits<ProblemDocument> searchHits = elasticsearchTemplate.search(query, ProblemDocument.class);
        return searchHits.getSearchHits().stream()
                .map(hit -> hit.getContent().getId())
                .collect(Collectors.toList());
    }

    static List<String> normalizeTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return List.of();
        }
        Set<String> normalized = new LinkedHashSet<>();
        for (String tag : tags) {
            if (!StringUtils.hasText(tag)) {
                continue;
            }
            normalized.add(tag.trim());
        }
        return new ArrayList<>(normalized);
    }

    static boolean shouldResyncEs(long esCount, long dbCount) {
        return esCount == 0 && dbCount > 0;
    }

    private long safeEsCount() {
        try {
            return problemRepository.count();
        } catch (Exception e) {
            log.warn("查询 ES count 失败，尝试重新创建索引", e);
            recreateIndex();
            return 0L;
        }
    }

    private void attachTags(List<Problem> problems) {
        if (problems == null || problems.isEmpty()) {
            return;
        }

        List<Long> problemIds = problems.stream()
                .map(Problem::getId)
                .filter(id -> id != null)
                .collect(Collectors.toList());
        if (problemIds.isEmpty()) {
            return;
        }

        List<ProblemTagView> tagViews = problemTagMapper.selectTagsByProblemIds(problemIds);
        Map<Long, List<String>> tagMap = new HashMap<>();
        for (ProblemTagView view : tagViews) {
            tagMap.computeIfAbsent(view.getProblemId(), key -> new ArrayList<>()).add(view.getTagName());
        }

        for (Problem problem : problems) {
            problem.setTags(tagMap.getOrDefault(problem.getId(), List.of()));
        }
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
    @Override
    public int syncAllToElasticsearch() {
        log.info("开始全量同步数据到 ES");
        List<Problem> allProblems = super.list();

        List<ProblemDocument> documents = allProblems.stream()
                .map(problem -> new ProblemDocument(problem.getId(), problem.getName(), problem.getDescription()))
                .collect(Collectors.toList());
        log.info("全量同步完成,共同步 {} 条数据", documents.size());
        problemRepository.saveAll(documents);
        return documents.size();
    }

    @Override
    public int reindexAll() {
        recreateIndex();
        return syncAllToElasticsearch();
    }

    @Override
    public void resetProblems() {
        log.info("开始清空 problem 模块数据...");
        problemTagMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>());
        tagMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>());
        super.remove(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>());
        recreateIndex();
        log.info("problem 数据与 ES 索引已清空");
    }

    private void recreateIndex() {
        IndexOperations indexOps = elasticsearchTemplate.indexOps(ProblemDocument.class);
        if (indexOps.exists()) {
            indexOps.delete();
        }
        indexOps.create();
        indexOps.putMapping(indexOps.createMapping());
    }
}
