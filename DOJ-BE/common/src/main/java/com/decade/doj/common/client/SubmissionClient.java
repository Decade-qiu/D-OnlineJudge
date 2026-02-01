package com.decade.doj.common.client;

import com.decade.doj.common.domain.R;
import com.decade.doj.common.domain.po.Submission;
import com.decade.doj.common.domain.vo.SubmissionStatsVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("submission-service")
public interface SubmissionClient {

    @PostMapping("/submission/submit")
    R<Long> submit(@RequestBody Submission submission);

    @GetMapping("/submission/stats")
    R<SubmissionStatsVO> getStats();

}
