package com.github.bigmouth.cn.talkx.actions;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * @author allen
 * @date 2023/6/26
 * @since 1.0.0
 */
public class UpdateTalkxAction extends NotificationAction {

    public UpdateTalkxAction(@Nullable @NlsContexts.NotificationContent String text) {
        super(text);
    }

    public UpdateTalkxAction(@NotNull Supplier<@NlsContexts.NotificationContent String> dynamicText) {
        super(dynamicText);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
        Project project = e.getData(LangDataKeys.PROJECT);
        ShowSettingsUtil.getInstance().showSettingsDialog(project, "Plugins");
    }
}
