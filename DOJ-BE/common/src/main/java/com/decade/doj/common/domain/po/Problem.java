package com.decade.doj.common.domain.po;

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
@Accessors(chain = true)
@Schema(description="Problem对象")
public class Problem {

    private Long id;

    private String name;

    private String inputStyle;

    private String outputStyle;

    private String dataRange;

    private String inputSample;

    private String outputSample;

    private String difficulty;

    private Integer timeLimit;

    private Integer memoryLimit;

    private String description;

    private Integer totalPass;

    private Integer totalAttempt;

    private String tag;

    private String testData;

    private String testAns;
}