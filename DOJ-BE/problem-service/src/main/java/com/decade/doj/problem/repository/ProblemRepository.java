package com.decade.doj.problem.repository;

import com.decade.doj.problem.domain.document.ProblemDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProblemRepository extends ElasticsearchRepository<ProblemDocument, Long> {
}
