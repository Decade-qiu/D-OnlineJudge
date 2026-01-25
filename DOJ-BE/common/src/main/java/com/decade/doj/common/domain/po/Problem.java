package com.decade.doj.common.domain.po;

import com.decade.doj.common.domain.json.StringListDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
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

    private String description;

    private String inputStyle;

    private String outputStyle;

    @JsonDeserialize(using = StringListDeserializer.class)
    private List<String> inputSample;

    @JsonDeserialize(using = StringListDeserializer.class)
    private List<String> outputSample;

    private String hint;

    private String difficulty;

    private Integer timeLimit;

    private Integer memoryLimit;

    private Integer totalPass;

    private Integer totalAttempt;

    private List<String> tags;

    private String testData;

    private String testAns;
}
