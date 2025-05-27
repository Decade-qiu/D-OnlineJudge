package com.decade.doj.problem;

import com.decade.doj.problem.domain.po.Problem;
import com.decade.doj.problem.service.IProblemService;
import com.decade.doj.problem.service.impl.ProblemServiceImpl;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestProblem {

    @Autowired
    private IProblemService problemService;

    @Test
    public void test() {
        Problem problem = problemService.getById(1);
        System.out.println(problem);
        System.out.println("fin");
    }

}
