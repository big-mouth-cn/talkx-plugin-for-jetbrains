package com.github.bigmouth.cn.talkx;

import com.github.bigmouth.cn.talkx.services.NotificationService;
import com.github.bigmouth.cn.talkx.services.TalkxWindowService;
import com.github.bigmouth.cn.talkx.setting.Constant;
import com.github.bigmouth.cn.talkx.windows.RefreshTalkxAction;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.cef.browser.CefBrowser;
import org.cef.handler.CefLifeSpanHandlerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.function.Consumer;

/**
 * @author allen
 * @date 2023/5/26
 * @since 1.0.0
 */
public class TalkxWindowFactory implements ToolWindowFactory {

    private static final Logger logger = Logger.getInstance(TalkxWindowFactory.class);

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        logger.info("Creating tool window content");
        TalkxWindowService talkxWindowService = project.getService(TalkxWindowService.class);

        JPanel csPanel = new JPanel(new BorderLayout());
        String labelText =
                "<html>" +
                        "<body style=\"margin: 20px 20px; line-height: 26px; \">" +
                        "<div>" +
                        "糟糕！TalkX 被意外地关闭了。<br>" +
                        "请点击右上角的「刷新」按钮后耐心等待数秒钟。<br>" +
                        "如果多次尝试后仍然无法显示，请将下面的信息<a href=\"mailto:huxiao.mail@qq.com\">发送给我们</a>。" +
                        "</div>" +
                        "<hr style='margin:5px 0px;'>" +
                        "<div>" +
                        "操作系统：" + SystemInfo.getOsNameAndVersion() + "<br>" +
                        "软件版本：" + Constant.IDE_VERSION + "<br>" +
                        "插件版本：" + Constant.TALKX_VERSION + "<br>" +
                        "</div>" +
                        "</body>" +
                        "</html>";
        JLabel label = new JLabel(labelText);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.TOP);

//        csPanel.add(label, BorderLayout.CENTER);

        csPanel.add(talkxWindowService.getTalkxWindow().getWebViewOrElseCreating());

        RefreshTalkxAction refreshTalkxAction = new RefreshTalkxAction(talkxWindowService, csPanel);
        refreshTalkxAction.setWebviewRender(new Consumer<JComponent>() {
            @Override
            public void accept(JComponent jComponent) {
                logger.info("Created: " + jComponent);
            }
        });

        toolWindow.setTitleActions(Collections.singletonList(refreshTalkxAction));

        ContentManager contentManager = toolWindow.getContentManager();
        Content content = contentManager.getFactory().createContent(csPanel, "", false);
        contentManager.addContent(content);

        // 添加关闭后恢复到初始状态。
        talkxWindowService.setCefLifeSpanHandler(new CefLifeSpanHandlerAdapter() {
            @Override
            public void onBeforeClose(CefBrowser browser) {
                try {
                    csPanel.removeAll();
                    csPanel.add(label, BorderLayout.CENTER);
                    logger.info("CefBrowser on closed!");
                } catch (Exception e) {
                    logger.error("onClose: ", e);
                }
            }
        });

        try {
            Notification notification = NotificationService.createNotification("TalkX", "", "如果遇到TalkX无法正常显示，请点击插件右上方刷新按钮重新加载。",
                    NotificationType.INFORMATION, null);
            NotificationService.notifyNotification(project, notification);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException ignored) {
        }
    }
}
