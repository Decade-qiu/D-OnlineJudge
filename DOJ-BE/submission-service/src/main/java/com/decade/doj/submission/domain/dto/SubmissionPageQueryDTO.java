package com.decade.doj.submission.domain.dto;

import com.decade.doj.common.domain.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class SubmissionPageQueryDTO extends PageQueryDTO {

    @Schema(description = "提交ID")
    private Long submissionId;

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "题目ID")
    private String problemId;

    @Schema(description = "语言")
    private String language;

    @Schema(description = "判题状态")
    private String status;
}
