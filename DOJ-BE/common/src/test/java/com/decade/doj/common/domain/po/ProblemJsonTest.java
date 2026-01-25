package com.decade.doj.common.domain.po;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProblemJsonTest {

    @Test
    void shouldDeserializeSampleArrays() throws Exception {
        String json = "{\"id\":1,\"name\":\"A+B\",\"inputSample\":[\"1 2\"],\"outputSample\":[\"3\"]}";
        ObjectMapper mapper = new ObjectMapper();
        Problem problem = mapper.readValue(json, Problem.class);

        assertEquals(List.of("1 2"), problem.getInputSample());
        assertEquals(List.of("3"), problem.getOutputSample());
    }
}
