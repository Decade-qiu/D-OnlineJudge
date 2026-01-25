package com.decade.doj.problem.domain.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import java.io.Serializable;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author 
 * @since 2024-09-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "problem", autoResultMap = true)
@Schema(description="Problem对象")
public class Problem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("name")
    private String name;

    @TableField("description")
    private String description;

    @TableField("input_style")
    private String inputStyle;

    @TableField("output_style")
    private String outputStyle;

    @TableField(value = "input_sample", typeHandler = JacksonTypeHandler.class)
    private java.util.List<String> inputSample;

    @TableField(value = "output_sample", typeHandler = JacksonTypeHandler.class)
    private java.util.List<String> outputSample;

    @TableField("hint")
    private String hint;

    @TableField("difficulty")
    private String difficulty;

    @TableField("time_limit")
    private Integer timeLimit;

    @TableField("memory_limit")
    private Integer memoryLimit;

    @TableField("total_pass")
    private Integer totalPass;

    @TableField("total_attempt")
    private Integer totalAttempt;

    @TableField(exist = false)
    private java.util.List<String> tags;

    @TableField(exist = false)
    private String status;

    @TableField("test_data")
    private String testData;

    @TableField("test_ans")
    private String testAns;
}
