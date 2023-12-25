package com.github.bigmouth.cn.talkx.windows;

import com.github.bigmouth.cn.talkx.services.TalkxWindowService;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.function.Consumer;

/**
 * @author huxiao
 * @date 2023/10/9
 * @since 1.0.0
 */
public class RefreshTalkxAction extends AnAction {

    private final TalkxWindowService talkxWindowService;
    private final JPanel panel;
    private Consumer<JComponent> webviewRender;

    public RefreshTalkxAction(TalkxWindowService talkxWindowService, JPanel panel) {
        super("刷新", "", AllIcons.Actions.Refresh);
        this.talkxWindowService = talkxWindowService;
        this.panel = panel;
    }

    @Override
    public synchronized void actionPerformed(@NotNull AnActionEvent e) {
        System.out.println("Do refresh CefBrowser...");
        talkxWindowService.destroy();

        JComponent webview = talkxWindowService.getTalkxWindow().getWebViewOrElseCreating();

        panel.removeAll();
        panel.add(webview);

        webview.setVisible(false);
        webview.setVisible(true);

        webviewRender.accept(webview);
    }

    public Consumer<JComponent> getWebviewRender() {
        return webviewRender;
    }

    public void setWebviewRender(Consumer<JComponent> webviewRender) {
        this.webviewRender = webviewRender;
    }
}
