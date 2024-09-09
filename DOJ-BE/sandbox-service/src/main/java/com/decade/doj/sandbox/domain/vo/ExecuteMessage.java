package com.decade.doj.sandbox.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ExecuteMessage {

    private Integer exitValue;

    private String message;

    private double time;

    private Long memory;
}
