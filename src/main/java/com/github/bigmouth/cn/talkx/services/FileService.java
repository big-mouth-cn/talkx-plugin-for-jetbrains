package com.github.bigmouth.cn.talkx.services;

import com.github.bigmouth.cn.talkx.utils.GenericUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * @author allen
 * @date 2023/6/14
 * @since 1.0.0
 */
public class FileService {
    public FileService() {
    }

    public static File replaceContentInFile(File file, String input, String replace, int startLine, int endLine) {
        try {
            if (input.indexOf("\r\n") > 0) {
                input = input.replaceAll("\r\n", System.lineSeparator());
            } else if (input.indexOf("\n\r") > 0) {
                input = input.replaceAll("\n\r", System.lineSeparator());
            } else if (input.indexOf("\n") > 0) {
                input = input.replaceAll("\n", System.lineSeparator());
            } else if (input.indexOf("\r") > 0) {
                input = input.replaceAll("\r", System.lineSeparator());
            }

            if (replace.indexOf("\r\n") > 0) {
                replace = replace.replaceAll("\r\n", System.lineSeparator());
            } else if (replace.indexOf("\n\r") > 0) {
                replace = replace.replaceAll("\n\r", System.lineSeparator());
            } else if (replace.indexOf("\n") > 0) {
                replace = replace.replaceAll("\n", System.lineSeparator());
            } else if (replace.indexOf("\r") > 0) {
                replace = replace.replaceAll("\r", System.lineSeparator());
            }

            FileReader fr = new FileReader(file);
            String totalStr = "";
            String toReplace = "";
            int lineNumber = 0;
            BufferedReader br = new BufferedReader(fr);

            File var12;
            try {
                String s;
                while((s = br.readLine()) != null) {
                    ++lineNumber;
                    if (lineNumber >= startLine && lineNumber < endLine) {
                        toReplace = toReplace + s + System.lineSeparator();
                    } else if (lineNumber == endLine) {
                        toReplace = toReplace + s + System.lineSeparator();
                        toReplace = toReplace.replace(input, replace);
                        totalStr = totalStr + toReplace;
                    } else {
                        totalStr = totalStr + s + System.lineSeparator();
                    }
                }

                FileWriter fw = new FileWriter(file);
                fw.write(totalStr);
                fw.close();
                var12 = file;
            } catch (Throwable var14) {
                try {
                    br.close();
                } catch (Throwable var13) {
                    var14.addSuppressed(var13);
                }

                throw var14;
            }

            br.close();
            return var12;
        } catch (FileNotFoundException var15) {
            var15.printStackTrace();
        } catch (Exception var16) {
            var16.printStackTrace();
        }

        return file;
    }

    public static String getFilePathInProperFormat(String filePath, String gitRootPath) {
        if (filePath.contains(".idea")) {
            filePath = filePath.split(".idea")[1];
            filePath = gitRootPath + filePath;
        }

        return filePath;
    }

    public static File deleteFile(File file) {
        try {
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception var2) {
            var2.printStackTrace();
        }

        return file;
    }

    public File getBlankFile() throws IOException {
        File blankFile = null;
        String tempDir = System.getProperty("java.io.tmpdir");
        String tempFilePath = tempDir + File.separator + "blankfile.txt";
        blankFile = new File(tempFilePath);
        if (!blankFile.exists()) {
            blankFile.createNewFile();
        }

        return blankFile;
    }

    public boolean isFilePresentInUserTemp(Object request) {
        boolean isFilePresentInUserTemp = false;
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap requestMap = (HashMap)objectMapper.convertValue(request, HashMap.class);
        String fileName = requestMap.get("fileName").toString();
        String tempDirectoryName = null;
        String defaultBaseDir = null;

        try {
            defaultBaseDir = System.getProperty("java.io.tmpdir");
            tempDirectoryName = GenericUtils.getTempFileDirectoryName(requestMap);
            Path path = Paths.get(defaultBaseDir + File.separator + tempDirectoryName);
            Path tempFile = Files.createDirectories(path);
            File patchFile = new File(tempFile + File.separator + fileName);
            if (patchFile != null && patchFile.exists()) {
                isFilePresentInUserTemp = true;
            }
        } catch (IOException var11) {
            var11.printStackTrace();
            isFilePresentInUserTemp = false;
        }

        return isFilePresentInUserTemp;
    }

    public File copyFileContent(File fileFrom, File fileTo) throws IOException {
        FileInputStream in = null;
        FileOutputStream out = null;

        try {
            fileTo.setWritable(true);
            in = new FileInputStream(fileFrom);
            out = new FileOutputStream(fileTo);

            while(true) {
                int n;
                if ((n = in.read()) == -1) {
                    fileTo.setWritable(false);
                    break;
                }

                out.write(n);
            }
        } finally {
            if (in != null) {
                in.close();
            }

            if (out != null) {
                out.close();
            }

        }

        System.out.println("File Copied");
        return fileTo;
    }

    public File copyFileContentForWrite(File fileFrom, File fileTo) throws IOException {
        FileInputStream in = null;
        FileOutputStream out = null;

        try {
            fileTo.setWritable(true);
            in = new FileInputStream(fileFrom);
            out = new FileOutputStream(fileTo);

            while(true) {
                int n;
                if ((n = in.read()) == -1) {
                    fileTo.setWritable(true);
                    break;
                }

                out.write(n);
            }
        } finally {
            if (in != null) {
                in.close();
            }

            if (out != null) {
                out.close();
            }

        }

        System.out.println("File Copied");
        return fileTo;
    }
}
