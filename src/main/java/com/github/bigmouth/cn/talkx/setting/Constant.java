package com.github.bigmouth.cn.talkx.setting;

import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.extensions.PluginId;

/**
 * @author allen
 * @date 2023/5/30
 * @since 1.0.0
 */
public interface Constant {

//    String WEB_URL = "http://yy.hangzhouyiyao.cn/talkx-plugin-web/index.html";
    String WEB_URL = "https://plugin-web.talkx.cn/index.html";

    String IDE_VERSION = ApplicationInfo.getInstance().getFullApplicationName();
    String TALKX_VERSION = PluginManagerCore.getPlugin(PluginId.findId("com.github.bigmouthcn.talkxideaplugin")).getVersion();
}
