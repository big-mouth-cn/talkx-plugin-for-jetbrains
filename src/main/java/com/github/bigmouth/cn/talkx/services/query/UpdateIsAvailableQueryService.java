package com.github.bigmouth.cn.talkx.services.query;

import com.github.bigmouth.cn.talkx.actions.UpdateTalkxAction;
import com.github.bigmouth.cn.talkx.services.NotificationService;
import com.google.gson.Gson;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;

import java.lang.reflect.InvocationTargetException;

/**
 * @author allen
 * @date 2023/6/26
 * @since 1.0.0
 */
public class UpdateIsAvailableQueryService extends AbstractInvokeLaterQueryService {

    @Override
    protected void invokeOnLater(QueryInvokeArgument queryInvokeArgument) {
        Project project = queryInvokeArgument.getProject();
        String data = queryInvokeArgument.getData();
        UpdateIsAvailable updateIsAvailable = new Gson().fromJson(data, UpdateIsAvailable.class);
        String talkX = "TalkX";
        String subTitle = "更新可用";
        String content = "TalkX的最新版本 " + updateIsAvailable.getLastVersion() + " 已经发布。请从市场更新到最新版本以获得更好的体验。";
        try {
            Notification notificationWarning = NotificationService.createNotification(talkX, subTitle, content, NotificationType.WARNING, new UpdateTalkxAction("更新TalkX"));
            NotificationService.notifyNotification(project, notificationWarning);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String key() {
        return "update_is_available";
    }

    public static class UpdateIsAvailable {
        private String lastVersion;
        private String installVersion;

        public String getLastVersion() {
            return lastVersion;
        }

        public UpdateIsAvailable setLastVersion(String lastVersion) {
            this.lastVersion = lastVersion;
            return this;
        }

        public String getInstallVersion() {
            return installVersion;
        }

        public UpdateIsAvailable setInstallVersion(String installVersion) {
            this.installVersion = installVersion;
            return this;
        }
    }
}
