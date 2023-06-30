package com.github.bigmouth.cn.talkx.services.query;

import com.github.bigmouth.cn.talkx.utils.FileUtils;
import com.google.gson.Gson;

/**
 * @author allen
 * @date 2023/6/15
 * @since 1.0.0
 */
public class ReadFromFileQueryService implements QueryService {
    @Override
    public String key() {
        return "read-from-file";
    }

    @Override
    public String invoke(QueryInvokeArgument queryInvokeArgument) throws Exception {
        String data = queryInvokeArgument.getData();
        ReadFromFile readFromFile = new Gson().fromJson(data, ReadFromFile.class);
        return FileUtils.readFromFile(readFromFile.getFileName());
    }

    public static class ReadFromFile {
        private String fileName;

        public String getFileName() {
            return fileName;
        }

        public ReadFromFile setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }
    }
}
