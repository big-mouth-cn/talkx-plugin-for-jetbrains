package com.github.bigmouth.cn.talkx.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author allen
 * @date 2023/6/14
 * @since 1.0.0
 */
public class FileUtils {

    private static final String DATA_DIR = System.getProperty("user.home") + File.separator + ".talkx" + File.separator + "data" + File.separator;

    public static void writeToFile(String fileName, String content, boolean append) throws IOException {
        String filePath = DATA_DIR + fileName;
        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        org.apache.commons.io.FileUtils.writeStringToFile(file, content, StandardCharsets.UTF_8, append);
    }

    public static String readFromFile(String fileName) throws IOException {
        String filePath = DATA_DIR + fileName;
        File file = new File(filePath);
        return org.apache.commons.io.FileUtils.readFileToString(file, StandardCharsets.UTF_8);
    }
}
