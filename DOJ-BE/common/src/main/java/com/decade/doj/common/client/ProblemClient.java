package com.decade.doj.common.client;

import com.decade.doj.common.domain.R;
import com.decade.doj.common.domain.po.Problem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("problem-service")
public interface ProblemClient {

    @GetMapping("/problem/{id}")
    R<Problem> getProblemById(@PathVariable Long id);

}
