package com.github.bigmouth.cn.talkx.services;

import com.intellij.icons.AllIcons;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;

import java.lang.reflect.InvocationTargetException;

/**
 * @author allen
 * @date 2023/5/30
 * @since 1.0.0
 */
public class NotificationService {
    public static Notification createNotification(String title, String subTitle, String content, NotificationType type, NotificationAction action) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Notification notification = NotificationGroupManager.getInstance().getNotificationGroup("TalkX Notification Group").createNotification(content, type);
        notification.setTitle(title);
        notification.setSubtitle(subTitle);
        if (type.equals(NotificationType.ERROR)) {
            notification.setIcon(AllIcons.General.NotificationError);
        } else if (type.equals(NotificationType.WARNING)) {
            notification.setIcon(AllIcons.General.NotificationWarning);
        } else if (type.equals(NotificationType.INFORMATION)) {
            notification.setIcon(AllIcons.General.NotificationInfo);
        }

        if (action != null) {
            notification.addAction(action);
        }

        return notification;
    }

    public static void notifyNotification(Project project, Notification notification) {
        notification.notify(project);
    }
}
