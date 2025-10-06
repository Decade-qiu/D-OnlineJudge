package com.decade.doj.sandbox.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class JudgingTask {
    Long submissionId;
    String localPath;
    String input;
    String output;
    String filename;
    String lang;
    Long problemId;
    String code;
    Long uid;
}
