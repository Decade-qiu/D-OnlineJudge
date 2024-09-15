package com.decade.doj.sandbox.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ExecuteMessage {

    private Integer exitValue;

    private String status;

    private String message;

    private double time;

    private Long memory;

    public static String getStatus(Integer exitValue){
        if (exitValue == null) {
            return "Unknown Error";
        }
        if (exitValue == 0) {
            return "Finished";
        }else if (exitValue == 2) {
            return "Compile Error";
        }else if (exitValue == 1) {
            return "Runtime Error";
        }else if (exitValue == 3) {
            return "Time Limit Exceeded";
        }else if (exitValue == 4) {
            return "Memory Limit Exceeded";
        }
        return "Unknown exitValue";
    }
}
