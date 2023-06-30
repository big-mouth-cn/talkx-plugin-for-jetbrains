package com.github.bigmouth.cn.talkx.services.query;

import com.google.gson.Gson;
import com.intellij.ide.BrowserUtil;

/**
 * @author allen
 * @date 2023/6/13
 * @since 1.0.0
 */
public class OpenExternalWebPageQueryService implements QueryService {
    @Override
    public String key() {
        return "open-external-web-page";
    }

    @Override
    public String invoke(QueryInvokeArgument queryInvokeArgument) {
        String data = queryInvokeArgument.getData();
        OpenExternalWebPage openExternalWebPage = new Gson().fromJson(data, OpenExternalWebPage.class);
        BrowserUtil.browse(openExternalWebPage.getUrl());
        return "success";
    }

    public static class OpenExternalWebPage {
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
