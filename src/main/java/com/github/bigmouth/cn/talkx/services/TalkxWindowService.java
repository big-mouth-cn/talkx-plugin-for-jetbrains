package com.github.bigmouth.cn.talkx.services;

import com.github.bigmouth.cn.talkx.windows.TalkxWindow;
import com.google.gson.JsonObject;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.cef.browser.CefBrowser;
import org.jetbrains.annotations.NotNull;

/**
 * @author allen
 * @date 2023/5/30
 * @since 1.0.0
 */
@Service
public final class TalkxWindowService {

    private final Project project;
    private final TalkxWindow talkxWindow;

    public Project getProject() {
        return this.project;
    }

    public TalkxWindow getTalkxWindow() {
        return this.talkxWindow;
    }

    public TalkxWindowService(@NotNull Project project) {
        this.project = project;
        this.talkxWindow = new TalkxWindow(project);
    }

    public void notifyIdeAppInstance(@NotNull JsonObject postMessage) {
        CefBrowser browser = this.getTalkxWindow().webView().getCefBrowser();
        browser.executeJavaScript("window.postMessage(" + postMessage + ",'*');", browser.getURL(), 0);
    }
}
