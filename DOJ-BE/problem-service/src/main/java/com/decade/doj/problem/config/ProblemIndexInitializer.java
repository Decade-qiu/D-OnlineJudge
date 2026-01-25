package com.decade.doj.problem.config;

import com.decade.doj.problem.domain.document.ProblemDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProblemIndexInitializer {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    @PostConstruct
    public void initIndex() {
        IndexOperations indexOps = elasticsearchTemplate.indexOps(ProblemDocument.class);
        if (!indexOps.exists()) {
            indexOps.create();
            indexOps.putMapping(indexOps.createMapping());
            log.info("Created Elasticsearch index: {}", indexOps.getIndexCoordinates().getIndexName());
        }
    }
}
