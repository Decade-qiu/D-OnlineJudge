package com.decade.doj.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.File;

public class LocalResource {

    public static String getLocalFilePath(String resourcePath) {
        try {
            ClassPathResource resource = new ClassPathResource(resourcePath);
            File file = resource.getFile();  // 获取文件对象
            return file.getAbsolutePath().replace("\\", "/");   // 返回文件的绝对路径
        } catch (Exception e) {
            System.out.println("获取文件失败"+e);
            return null;
        }
    }

}
