package com.decade.doj.sandbox.enums;

import lombok.Setter;

import java.util.Arrays;
import java.util.List;

public enum LanguageEnum {

    PYTHON("python3 %s.py", "python", "128m", 5, "code-runner-python"),
    JAVA("sh -c 'javac %s.java && java -Xmx1024m %s'", "java", "128m", 2, "code-runner-java"),
    CPP("sh -c 'g++ -std=c++17 %s.cpp -o %s.out && ./%s.out'", "cpp", "128m", 1, "code-runner-cpp");

    private final String runCmd;
    @Setter
    private String memoryLimit;
    @Setter
    private int timeLimit;
    private final String language;
    private final String imageName;

    LanguageEnum(String runCmd, String language, String memoryLimit, int timeLimit, String imageName) {
        this.runCmd = runCmd;
        this.language = language;
        this.memoryLimit = memoryLimit;
        this.timeLimit = timeLimit;
        this.imageName = imageName;
    }

    public String getRunCmd() {
        return runCmd;
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

    public String getImageName() {
        return imageName;
    }

    public static final List<LanguageEnum> values = Arrays.asList(JAVA, PYTHON, CPP);

    public static boolean isInValidLanguage(String lang) {
        for (LanguageEnum language : LanguageEnum.values) {
            if (language.getLanguage().equalsIgnoreCase(lang)) {
                return false;
            }
        }
        return true;
    }

    public static LanguageEnum getLanguageEnum(String lang) {
        for (LanguageEnum language : LanguageEnum.values) {
            if (language.getLanguage().equalsIgnoreCase(lang)) {
                return language;
            }
        }
        return null;
    }

}
