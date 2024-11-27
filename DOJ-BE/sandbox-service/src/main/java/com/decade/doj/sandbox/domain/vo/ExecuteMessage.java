package com.decade.doj.sandbox.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Data
@Accessors(chain = true)
public class ExecuteMessage {

    private Integer exitValue;
    private String status;
    private String message;
    private double time;
    private Long memory;

    private static final Map<Integer, String> exitStatusMap = new HashMap<>();

    static {
        exitStatusMap.put(0, "Finished");
        exitStatusMap.put(2, "Compile Error");
        exitStatusMap.put(1, "Runtime Error");
        exitStatusMap.put(3, "Time Limit Exceeded");
        exitStatusMap.put(4, "Memory Limit Exceeded");
        exitStatusMap.put(10, "Accepted");
        exitStatusMap.put(11, "Wrong Answer");
    }

    public static String getStatus(Integer exitValue) {
        if (exitValue == null) {
            return "Unknown Error";
        }
        return exitStatusMap.getOrDefault(exitValue, "Unknown exitValue");
    }
}