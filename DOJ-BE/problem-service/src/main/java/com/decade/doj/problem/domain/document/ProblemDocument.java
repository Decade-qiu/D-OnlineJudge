package com.decade.doj.problem.domain.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "doj_problem")
@Setting(settingPath = "es/problem-settings.json")
public class ProblemDocument {

    @Id
    private Long id;

    // 题目名称，全文检索（大小写不敏感）
    @Field(type = FieldType.Text, analyzer = "ik_max_lowercase", searchAnalyzer = "ik_smart_lowercase")
    private String name;

    // 题目描述，全文检索
    @Field(type = FieldType.Text, analyzer = "ik_max_lowercase", searchAnalyzer = "ik_smart_lowercase")
    private String description;
}
