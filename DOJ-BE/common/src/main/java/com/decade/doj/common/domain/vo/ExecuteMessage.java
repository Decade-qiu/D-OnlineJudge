package com.decade.doj.common.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@Accessors(chain = true)
public class ExecuteMessage {

    private Integer exitValue;
    private String status;
    private String message;
    private double time;
    private Long memory;

    private static final Map<Integer, String> exitStatusMap = new HashMap<>();
    private static final Set<Integer> InfoStatus = new HashSet<>();

    static {
        exitStatusMap.put(0, "Finished");
        exitStatusMap.put(1, "Runtime Error");
        exitStatusMap.put(2, "Compile Error");
        exitStatusMap.put(124, "Time Limit Exceeded");
        exitStatusMap.put(137, "Memory Limit Exceeded");
        exitStatusMap.put(10, "Accepted");
        exitStatusMap.put(11, "Wrong Answer");

        InfoStatus.add(0);
        InfoStatus.add(1);
        InfoStatus.add(2);
        InfoStatus.add(10);
        InfoStatus.add(11);
    }

    public static String getStatus(Integer exitValue) {
        if (exitValue == null) {
            return "Unknown Error";
        }
        return exitStatusMap.getOrDefault(exitValue, "Unknown exitValue");
    }

    public static boolean show(Integer exitValue) {
        return InfoStatus.contains(exitValue);
    }
}