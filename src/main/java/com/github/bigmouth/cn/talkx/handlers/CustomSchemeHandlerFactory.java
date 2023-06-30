package com.github.bigmouth.cn.talkx.handlers;

import com.intellij.openapi.project.Project;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefSchemeHandlerFactory;
import org.cef.handler.CefResourceHandler;
import org.cef.network.CefRequest;

/**
 * @author allen
 * @date 2023/6/1
 * @since 1.0.0
 */
public class CustomSchemeHandlerFactory implements CefSchemeHandlerFactory {

    private final Project project;

    public CustomSchemeHandlerFactory(Project project) {
        this.project = project;
    }

    @Override
    public CefResourceHandler create(CefBrowser browser, CefFrame frame, String schemeName, CefRequest request) {
        return new CustomResourceHandler(this.project);
    }
}
