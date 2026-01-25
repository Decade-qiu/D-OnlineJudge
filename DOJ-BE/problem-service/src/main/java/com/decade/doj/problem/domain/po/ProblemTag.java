package com.decade.doj.problem.domain.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("problem_tag")
@Schema(description = "题目与标签关联对象")
public class ProblemTag implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField("problem_id")
    private Long problemId;

    @TableField("tag_id")
    private Long tagId;
}
