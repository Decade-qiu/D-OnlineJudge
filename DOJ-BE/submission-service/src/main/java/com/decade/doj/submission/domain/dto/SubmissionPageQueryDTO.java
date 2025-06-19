package com.decade.doj.submission.domain.dto;

import com.decade.doj.common.domain.PageQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class SubmissionPageQueryDTO extends PageQueryDTO {

    private String userId;

    private String problemId;

    private String language;

    private String status;
}
