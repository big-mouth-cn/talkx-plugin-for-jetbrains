package com.github.bigmouth.cn.talkx.services.query;

import com.github.bigmouth.cn.talkx.bean.EditorInfo;
import com.github.bigmouth.cn.talkx.setting.Constant;
import com.github.bigmouth.cn.talkx.utils.GenericUtils;
import com.google.gson.Gson;

/**
 * @author allen
 * @date 2023/6/14
 * @since 1.0.0
 */
public class QueryEditorInfoQueryService implements QueryService {
    @Override
    public String key() {
        return "query-editor-info";
    }

    @Override
    public String invoke(QueryInvokeArgument queryInvokeArgument) {
        EditorInfo editorInfo = new EditorInfo();
        editorInfo.setProductName(Constant.IDE_VERSION);
        editorInfo.setTalkxVersion(Constant.TALKX_VERSION);
        editorInfo.setFontFamily(GenericUtils.getFontFamily());
        editorInfo.setFontSize(GenericUtils.getFontSize());
        editorInfo.setTheme(GenericUtils.getTheme());
        return new Gson().toJson(editorInfo);
    }
}
