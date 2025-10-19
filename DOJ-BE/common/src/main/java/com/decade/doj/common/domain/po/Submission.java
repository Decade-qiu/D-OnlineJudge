package com.decade.doj.common.domain.po;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class Submission {
    private Long id;
    private Long userId;
    private String userName;
    private Long problemId;
    private String problemName;
    private String language;
    private String code;
    private Integer exitValue;
    private String status;
    private String message;
    private Double time;
    private Long memory;
    private Date submitTime;
}