package com.decade.doj.sandbox.enums;

import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class LanguageEnum {

    private String language;
    private String dockerImage;
    private String dockerName;
    private String suffix;

    public LanguageEnum(String language, String image, String script, String suffix) {
        this.language = language;
        this.dockerImage = image;
        this.dockerName = script;
        this.suffix = suffix;
    }

    public static final LanguageEnum JAVA = new LanguageEnum("java", "openjdk:11-slim", "java11", ".java");
    public static final LanguageEnum PYTHON = new LanguageEnum("python", "python:3.8-slim", "python3", ".py");
    public static final LanguageEnum CPP = new LanguageEnum("cpp", "gcc:13", "gcc13", ".cpp");

    public static final List<LanguageEnum> values = Arrays.asList(JAVA, PYTHON, CPP);

}
