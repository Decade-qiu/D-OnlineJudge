package com.decade.doj.problem.domain.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "doj_problem")
public class ProblemDocument {

    @Id
    private Long id;

    // 题目名称，全文检索
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String name;

    // 难度，适合精确匹配
    @Field(type = FieldType.Keyword)
    private String difficulty;

    // 题目描述，全文检索
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String description;

    // 标签，可以使用分词
    @Field(type = FieldType.Text, analyzer = "ik_smart", searchAnalyzer = "ik_smart")
    private String tag;

    // 输入风格
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String inputStyle;

    // 输出风格
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String outputStyle;

    // 数据范围
    @Field(type = FieldType.Text)
    private String dataRange;

    // // 输入样例
    // @Field(type = FieldType.Text)
    // private String inputSample;
    //
    // // 输出样例
    // @Field(type = FieldType.Text)
    // private String outputSample;

    // 时间限制（ms）
    @Field(type = FieldType.Integer)
    private Integer timeLimit;

    // 内存限制（MB）
    @Field(type = FieldType.Integer)
    private Integer memoryLimit;

    // 总通过数
    @Field(type = FieldType.Integer)
    private Integer totalPass;

    // 总提交数
    @Field(type = FieldType.Integer)
    private Integer totalAttempt;

    // 测试数据
    @Field(type = FieldType.Text)
    private String testData;

    // 测试答案
    @Field(type = FieldType.Text)
    private String testAns;
}