package com.github.bigmouth.cn.talkx.windows;

import com.github.bigmouth.cn.talkx.handlers.CustomSchemeHandlerFactory;
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
import org.cef.CefApp;
import org.cef.CefClient;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author allen
 * @date 2023/5/30
 * @since 1.0.0
 */
public class TalkxWindow {

    private JBCefBrowser webView;
    private final Project project;
    private boolean webViewLoaded;

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

                JBCefBrowser browser = new JBCefBrowser();

                this.registerAppSchemeHandler();
                String webUrl = Constant.WEB_URL;

                webUrl += "?productName=" + GenericUtils.urlEncode(Constant.IDE_VERSION);

                browser.loadURL(webUrl);

//                Disposer.register(getProject(), browser);
                this.webView = browser;
                CefClient cefClient = browser.getJBCefClient().getCefClient();
                cefClient.addMessageRouter(new TalkxWindowRouter().getCefMessageRouter(project));

                this.webViewLoaded = true;
                ApplicationManager.getApplication().invokeLater(() -> {
                    System.out.println("invoke Later!");
                });
            }
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        return this.webView;
    }

    public synchronized JBCefBrowser webView() {
        return !this.webViewLoaded ? this.webViewLazy() : this.webView;
    }

    public JComponent content() {
        return this.webView() != null ? this.webView().getComponent() : null;
    }

    private void registerAppSchemeHandler() {
        CustomSchemeHandlerFactory factory = new CustomSchemeHandlerFactory(this.getProject());
        CefApp.getInstance()
                .registerSchemeHandlerFactory("http", "ideapp", factory);
    }

    public TalkxWindow(@NotNull Project project) {
        this.webViewLoaded = false;
        this.project = project;
    }
}
