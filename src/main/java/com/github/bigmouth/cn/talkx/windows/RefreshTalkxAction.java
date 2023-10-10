package com.github.bigmouth.cn.talkx.windows;

import com.github.bigmouth.cn.talkx.services.TalkxWindowService;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author huxiao
 * @date 2023/10/9
 * @since 1.0.0
 */
public class RefreshTalkxAction extends AnAction {
    public RefreshTalkxAction() {
        super("Refresh TalkX", "", AllIcons.Actions.Refresh);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        e.getProject()
                .getService(TalkxWindowService.class)
                .getBrowser()
                .reloadIgnoreCache();
        System.out.println("refresh cef browser");
    }
}
