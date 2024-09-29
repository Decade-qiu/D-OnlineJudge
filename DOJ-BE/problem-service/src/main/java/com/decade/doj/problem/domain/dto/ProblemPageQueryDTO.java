package com.decade.doj.problem.domain.dto;

import com.decade.doj.common.domain.PageQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class ProblemPageQueryDTO extends PageQueryDTO {

    private String name;

    private String difficulty;

    private String tags;

    private String status;
}
