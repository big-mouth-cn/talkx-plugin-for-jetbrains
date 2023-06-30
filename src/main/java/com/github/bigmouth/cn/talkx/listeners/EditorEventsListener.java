package com.github.bigmouth.cn.talkx.listeners;

import com.github.bigmouth.cn.talkx.services.EditorService;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.fileEditor.impl.EditorWindow;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

/**
 * @author allen
 * @date 2023/6/14
 * @since 1.0.0
 */
public class EditorEventsListener implements FileEditorManagerListener {

    public EditorEventsListener() {
    }

    @Override
    public void selectionChanged(@NotNull FileEditorManagerEvent fileEditorManagerEvent) {
        Project project = fileEditorManagerEvent.getManager().getProject();
        ApplicationManager.getApplication().invokeLater(() -> {
            if (project.isInitialized() && project.isOpen() && !project.isDisposed()) {
                try {
                    FileEditorManagerEx manager = FileEditorManagerEx.getInstanceEx(project);
                    EditorService editorService = project.getService(EditorService.class);
                    VirtualFile newFile = fileEditorManagerEvent.getNewFile();
                    EditorWindow editorWindow = manager.getCurrentWindow();
                    if (editorWindow == null) {
                        return;
                    }

                    Editor editor = editorWindow.getManager().getSelectedTextEditor();
                    if (newFile != null && editorWindow.findFileComposite(newFile) != null) {
                        editorService.setActiveEditor(editor);
                        editorService.setActiveEditorWindow(editorWindow);
                        editorService.setActiveVirtualFile(newFile);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }, ModalityState.NON_MODAL);
    }
}