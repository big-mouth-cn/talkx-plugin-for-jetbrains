package com.github.bigmouth.cn.talkx.windows;

import com.github.bigmouth.cn.talkx.services.NotificationService;
import com.github.bigmouth.cn.talkx.setting.Constant;
import com.github.bigmouth.cn.talkx.utils.GenericUtils;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.jcef.JBCefApp;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.ui.jcef.JBCefClient;
import com.jetbrains.cef.JCefAppConfig;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefLifeSpanHandler;
import org.cef.handler.CefLoadHandlerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;
import java.util.Objects;

/**
 * @author allen
 * @date 2023/5/30
 * @since 1.0.0
 */
public class TalkxWindow {

    private JBCefBrowser webView;
    private final Project project;
    private boolean webViewLoaded;

    private CefLifeSpanHandler cefLifeSpanHandler;

    public Project getProject() {
        return this.project;
    }

    private JBCefBrowser webViewLazy() {
        try {
            System.out.println("JBCefApp.isSupported() == " + JBCefApp.isSupported());
            Notification browserNotification;
            if (!JBCefApp.isSupported()) {
                System.out.println("JCEF browser not supported by " + Constant.IDE_VERSION);
                String title = "TalkX: Attention Required";
                String content = "Please enable JCEF browser by enabling settings as per below. <br> 1. Go to Help -> Find action<br>2. Type -> 'Choose Boot Java Runtime for the IDE'.<br>3. From the dropdown 'New', select a Boot runtime which support JCEF browser.<br>4. Select ok and restart.\n<br>";
                browserNotification = NotificationService.createNotification(title, "", content, NotificationType.ERROR,
                    new NotificationAction("Click this link to see the instructions") {
                    @Override
                    public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {

                    }
                });
                NotificationService.notifyNotification(this.project, browserNotification);
            } else if (!this.webViewLoaded) {
                CefSettings cefSettings = JCefAppConfig.getInstance().getCefSettings();
                cefSettings.persist_session_cookies = true;
                cefSettings.cache_path = getCachePath();

                JBCefBrowser browser = new JBCefBrowser();

                String webUrl = Constant.WEB_URL;

                webUrl += "?productName=" + GenericUtils.urlEncode(Constant.IDE_VERSION)
                        + "&talkxVersion=" + Constant.TALKX_VERSION
                        + "&time=" + System.currentTimeMillis();

                browser.loadURL(webUrl);

                this.webView = browser;
                CefClient cefClient = browser.getJBCefClient().getCefClient();
                cefClient.addMessageRouter(new TalkxWindowRouter().getCefMessageRouter(project));

                this.webViewLoaded = true;

                ApplicationManager.getApplication().invokeLater(() -> {
                    System.out.println("invoke Later!");
                });

                JBCefClient jbCefClient = this.webView.getJBCefClient();
                if (Objects.nonNull(cefLifeSpanHandler)) {
                    jbCefClient.addLifeSpanHandler(cefLifeSpanHandler, this.webView.getCefBrowser());
                }
                jbCefClient.addLoadHandler(new CefLoadHandlerAdapter() {
                    @Override
                    public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {
                        System.out.println("onLoadEnd!");
                    }
                }, this.webView.getCefBrowser());

            }
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        return this.webView;
    }

    public void destroy() {
        if (this.webViewLoaded) {
            this.webViewLoaded = false;
            this.webView.dispose();
            this.webView = null;
        }
    }

    public synchronized JBCefBrowser webView() {
        return !this.webViewLoaded ? this.webViewLazy() : this.webView;
    }

    public JComponent getWebViewOrElseCreating() {
        return this.webView() != null ? this.webView().getComponent() : null;
    }

    public TalkxWindow(@NotNull Project project) {
        this.webViewLoaded = false;
        this.project = project;
    }

    private static String getCachePath() {
        return System.getProperty("user.home") + File.separator + ".talkx" + File.separator + "local_storage_cache";
    }
}
