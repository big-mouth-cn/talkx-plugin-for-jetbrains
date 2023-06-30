package com.github.bigmouth.cn.talkx.actions;

import com.github.bigmouth.cn.talkx.services.EditorHelper;
import com.github.bigmouth.cn.talkx.services.TalkxWindowService;
import com.google.gson.JsonObject;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.annotations.NotNull;

/**
 * @author allen
 * @date 2023/6/13
 * @since 1.0.0
 */
public abstract class AbstractCodeAction extends DumbAwareAction {

    protected abstract String getKey();

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();

        ApplicationManager.getApplication().invokeAndWait(() -> {
            VirtualFile virtualFile = e.getRequiredData(CommonDataKeys.VIRTUAL_FILE);
            Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
            JsonObject jsonObject = EditorHelper.getFileSelectionDetails(editor, true);
            ToolWindowManager windowManager = ToolWindowManager.getInstance(project);
            String actionKey = getKey();
            windowManager.getToolWindow("TalkX").activate(() -> {
                System.out.println(actionKey + " Enable TalkX window.");
            }, true, true);
            jsonObject.addProperty("cloudFile", "n");
            jsonObject.addProperty("isDiffView", "n");
            jsonObject.addProperty("fileName", virtualFile.getName());
            jsonObject.addProperty("filePath", virtualFile.getCanonicalPath());
            jsonObject.addProperty("isGitRepository", false);
            jsonObject.addProperty("initialGitFileStatus", "local");

            JsonObject result = new JsonObject();
            result.addProperty("key", actionKey);
            result.addProperty("data", jsonObject.toString());
            System.out.println(actionKey + " == " + result);
            project.getService(TalkxWindowService.class).notifyIdeAppInstance(result);
        });
    }
}
