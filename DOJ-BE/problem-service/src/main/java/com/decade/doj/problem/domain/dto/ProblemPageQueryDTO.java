package com.decade.doj.problem.domain.dto;

import com.decade.doj.common.domain.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class ProblemPageQueryDTO extends PageQueryDTO {

    @Schema(description = "题目名称")
    private String name;

    @Schema(description = "难度")
    private String difficulty;

    @Schema(description = "标签")
    private String tags;

    @Schema(description = "状态")
    private String status;
}
