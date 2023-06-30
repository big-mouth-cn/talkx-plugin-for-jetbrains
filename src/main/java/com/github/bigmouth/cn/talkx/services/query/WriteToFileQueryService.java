package com.github.bigmouth.cn.talkx.services.query;

import com.github.bigmouth.cn.talkx.utils.FileUtils;
import com.google.gson.Gson;

/**
 * @author allen
 * @date 2023/6/14
 * @since 1.0.0
 */
public class WriteToFileQueryService implements QueryService {
    @Override
    public String key() {
        return "write-to-file";
    }

    @Override
    public String invoke(QueryInvokeArgument queryInvokeArgument) throws Exception {
        String data = queryInvokeArgument.getData();
        WriteToFile writeToFile = new Gson().fromJson(data, WriteToFile.class);
        FileUtils.writeToFile(writeToFile.getFileName(), writeToFile.getContent(), writeToFile.isAppend());
        return "success";
    }

    public static class WriteToFile {
        private String fileName;
        private boolean append;
        private String content;

        public String getFileName() {
            return fileName;
        }

        public WriteToFile setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public boolean isAppend() {
            return append;
        }

        public WriteToFile setAppend(boolean append) {
            this.append = append;
            return this;
        }

        public String getContent() {
            return content;
        }

        public WriteToFile setContent(String content) {
            this.content = content;
            return this;
        }
    }
}
