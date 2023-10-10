package com.github.bigmouth.cn.talkx;

import com.github.bigmouth.cn.talkx.services.TalkxWindowService;
import com.github.bigmouth.cn.talkx.windows.RefreshTalkxAction;
import com.github.bigmouth.cn.talkx.windows.TalkxWindow;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;

/**
 * @author allen
 * @date 2023/5/26
 * @since 1.0.0
 */
public class TalkxWindowFactory implements ToolWindowFactory {

    Logger logger = Logger.getInstance(TalkxWindowFactory.class);

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        this.logger.info("Creating tool window content");
        toolWindow.setTitleActions(Collections.singletonList(new RefreshTalkxAction()));

        JPanel csPanel = new JPanel(new BorderLayout());
        TalkxWindowService talkxWindowService = project.getService(TalkxWindowService.class);
        TalkxWindow talkxWindow = talkxWindowService.getTalkxWindow();
        if (talkxWindow != null && talkxWindow.content() != null) {
            csPanel.add(talkxWindow.content());
            ContentManager contentManager = toolWindow.getContentManager();
            Content content = contentManager.getFactory()
                    .createContent(csPanel, "", false);
            contentManager.addContent(content);
        }
    }
}
