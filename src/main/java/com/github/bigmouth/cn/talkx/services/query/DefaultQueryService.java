package com.github.bigmouth.cn.talkx.services.query;

import com.github.bigmouth.cn.talkx.services.NotificationService;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

import java.util.function.BiConsumer;

/**
 * @author allen
 * @date 2023/6/13
 * @since 1.0.0
 */
public class DefaultQueryService implements QueryService {
    @Override
    public String key() {
        return null;
    }

    @Override
    public String invoke(QueryInvokeArgument queryInvokeArgument) throws Exception {
        Project project = queryInvokeArgument.getProject();
        String data = queryInvokeArgument.getData();
        Notification notification = NotificationService.createNotification("Message", "communication", data,
                NotificationType.INFORMATION,
                NotificationAction.create("Action", new BiConsumer<AnActionEvent, Notification>() {
                    @Override
                    public void accept(AnActionEvent anActionEvent, Notification notification) {

                    }
                }));
        NotificationService.notifyNotification(project, notification);
        return "success";
    }
}
