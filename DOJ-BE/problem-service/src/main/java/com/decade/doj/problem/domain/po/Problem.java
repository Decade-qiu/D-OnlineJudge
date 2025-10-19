package com.decade.doj.problem.domain.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

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
@TableName("problem")
@Schema(description="Problem对象")
public class Problem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("name")
    private String name;

    @TableField("input_style")
    private String inputStyle;

    @TableField("output_style")
    private String outputStyle;

    @TableField("data_range")
    private String dataRange;

    @TableField("input_sample")
    private String inputSample;

    @TableField("output_sample")
    private String outputSample;

    @TableField("difficulty")
    private String difficulty;

    @TableField("time_limit")
    private Integer timeLimit;

    @TableField("memory_limit")
    private Integer memoryLimit;

    @TableField("description")
    private String description;

    @TableField("total_pass")
    private Integer totalPass;

    @TableField("total_attempt")
    private Integer totalAttempt;

    @TableField("tag")
    private String tag;

    @TableField("test_data")
    private String testData;

    @TableField("test_ans")
    private String testAns;
}
