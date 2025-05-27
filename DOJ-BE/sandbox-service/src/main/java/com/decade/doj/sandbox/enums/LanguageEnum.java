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
    private Integer memoryLimit;
    private Integer timeLimit;

    public LanguageEnum(String language, String image, String script, String suffix, Integer memoryLimit, Integer timeLimit) {
        this.language = language;
        this.dockerImage = image;
        this.dockerName = script;
        this.suffix = suffix;
        this.memoryLimit = memoryLimit;
        this.timeLimit = timeLimit;
    }

    public static final LanguageEnum JAVA = new LanguageEnum("java", "openjdk:11-slim", "java11", ".java", 128, 4);
    public static final LanguageEnum PYTHON = new LanguageEnum("python", "python:3.8-slim", "python3", ".py", 128, 10);
    public static final LanguageEnum CPP = new LanguageEnum("cpp", "gcc:13", "gcc13", ".cpp", 128, 2);

    public static final List<LanguageEnum> values = Arrays.asList(JAVA, PYTHON, CPP);

    public static boolean isValidLanguage(String lang) {
        for (LanguageEnum language : LanguageEnum.values) {
            if (language.language.equalsIgnoreCase(lang)) {
                return true;
            }
        }
        return false;
    }

    public static LanguageEnum getLanguageEnum(String lang) {
        for (LanguageEnum language : LanguageEnum.values) {
            if (language.language.equalsIgnoreCase(lang)) {
                return language;
            }
        }
        return null;
    }

}
