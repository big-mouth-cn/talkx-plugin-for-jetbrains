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
public class UpdateIsRequiredQueryService extends AbstractInvokeLaterQueryService {

    @Override
    protected void invokeOnLater(QueryInvokeArgument queryInvokeArgument) {
        Project project = queryInvokeArgument.getProject();
        String data = queryInvokeArgument.getData();
        UpdateIsRequired updateIsRequiredAvailable = new Gson().fromJson(data, UpdateIsRequired.class);
        String talkX = "TalkX";
        String subTitle = "需要更新";
        String content = "TalkX的 " + updateIsRequiredAvailable.getInstallVersion() + " 版本在未来某个时候不再支持，请从市场更新到最新版本 " + updateIsRequiredAvailable.getLastVersion() + "。";
        try {
            Notification notificationWarning = NotificationService.createNotification(talkX, subTitle, content, NotificationType.ERROR, new UpdateTalkxAction("更新TalkX"));
            NotificationService.notifyNotification(project, notificationWarning);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String key() {
        return "update_is_required";
    }

    public static class UpdateIsRequired {
        private String lastVersion;
        private String installVersion;

        public String getLastVersion() {
            return lastVersion;
        }

        public UpdateIsRequired setLastVersion(String lastVersion) {
            this.lastVersion = lastVersion;
            return this;
        }

        public String getInstallVersion() {
            return installVersion;
        }

        public UpdateIsRequired setInstallVersion(String installVersion) {
            this.installVersion = installVersion;
            return this;
        }
    }
}
