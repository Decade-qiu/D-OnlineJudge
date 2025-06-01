package com.decade.doj.sandbox.enums;

import lombok.Data;

import java.util.Arrays;
import java.util.List;

public enum LanguageEnum {

    PYTHON("python3", "py", "python", "128m", 10),
    JAVA("java", "java", "java", "256m", 2),
    CPP("bash -c \"g++ Main.cpp && timeout 5s ./a.out\"", "cpp", "cpp", "128m", 1);

    private final String runCmd;
    private final String extension;
    private final String memoryLimit;
    private final int timeLimit;
    private final String language;

    LanguageEnum(String runCmd, String extension, String language, String memoryLimit, int timeLimit) {
        this.runCmd = runCmd;
        this.extension = extension;
        this.language = language;
        this.memoryLimit = memoryLimit;
        this.timeLimit = timeLimit;
    }

    public String getRunCmd() {
        return runCmd;
    }

    public String getExtension() {
        return extension;
    }

    public String getMemoryLimit() {
        return memoryLimit;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public String getLanguage() {
        return language;
    }

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
