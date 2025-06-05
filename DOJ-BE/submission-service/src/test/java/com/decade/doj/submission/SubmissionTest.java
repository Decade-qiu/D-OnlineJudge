package com.decade.doj.submission;

import com.decade.doj.submission.domain.po.Submission;
import com.decade.doj.submission.service.ISubmissionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
public class SubmissionTest {

    @Autowired
    private ISubmissionService submissionService;

    @Test
    public void testSubmissionService() {
        Submission submission = new Submission();
        submission.setUserId(3L);
        submission.setProblemId(1L);
        submission.setLanguage("Java");
        submission.setCode("public class Solution { public int add(int a, int b) { return a + b; } }");
        submission.setStatus("Wrong");
        submission.setMessage("Compilation Error");
        submission.setMemory(12L);
        submission.setTime(100.0);
        submission.setSubmitTime(new Date());

        boolean result = submissionService.save(submission);
        assert result : "Submission should be saved successfully";
    }
}
