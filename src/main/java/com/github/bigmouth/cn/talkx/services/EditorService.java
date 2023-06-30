package com.github.bigmouth.cn.talkx.services;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.impl.EditorComponentImpl;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.fileEditor.impl.EditorWindow;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.cef.callback.CefQueryCallback;

/**
 * @author allen
 * @date 2023/6/13
 * @since 1.0.0
 */
@Service
public final class EditorService {
    private final Logger logger = Logger.getInstance(EditorService.class);
    private Editor activeEditor;
    private VirtualFile activeVirtualFile;
    private EditorWindow activeEditorWindow;
    private final Project project;

    public EditorService(Project project) {
        this.project = project;
    }

    public Editor getActiveEditor() {
        return this.activeEditor;
    }

    public void setActiveEditor(Editor activeEditor) {
        this.activeEditor = activeEditor;
    }

    public VirtualFile getActiveVirtualFile() {
        return this.activeVirtualFile;
    }

    public void setActiveVirtualFile(VirtualFile activeVirtualFile) {
        this.activeVirtualFile = activeVirtualFile;
    }

    public EditorWindow getActiveEditorWindow() {
        return this.activeEditorWindow;
    }

    public void setActiveEditorWindow(EditorWindow activeEditorWindow) {
        this.activeEditorWindow = activeEditorWindow;
    }

    public void setDefault() {
        FileEditorManagerEx manager = FileEditorManagerEx.getInstanceEx(this.project);
        if (manager.getSelectedTextEditor() != null) {
            this.setActiveEditorWindow(manager.getCurrentWindow());
            this.setActiveEditor(manager.getSelectedTextEditor());
            this.setActiveVirtualFile(manager.getSelectedEditor().getFile());
            System.out.println("Set Active Editor" + this.getActiveEditor());
        }
    }

    public static void insertCodeInEditor(Project project, String text, CefQueryCallback callback) {
        try {
            EditorService editorService = (EditorService)project.getService(EditorService.class);
            Editor editor = editorService.getActiveEditor();
            if (editor == null) {
                editor = ((EditorComponentImpl)editorService.getActiveEditorWindow().getManager().getSelectedEditor().getPreferredFocusedComponent()).getEditor();
            }

            if (editor == null || editorService.getActiveEditorWindow().getSelectedFile() == null) {
                callback.failure(513, "Open a file in editor to perform Insert code");
                return;
            }

            Document document = ((Editor)editor).getDocument();
            Caret primaryCaret = ((Editor)editor).getCaretModel().getPrimaryCaret();
            int start = primaryCaret.getSelectionStart();
            int end = primaryCaret.getSelectionEnd();
            WriteCommandAction.runWriteCommandAction(project, () -> {
                document.replaceString(start, end, text);
            });
            primaryCaret.removeSelection();
            primaryCaret.setSelection(start, start + text.length());
        } catch (Exception var9) {
            var9.printStackTrace();
            callback.failure(500, var9.getMessage());
        }

    }

    public static void insertCodeInEditor(Project project, VirtualFile file, int start, int end, String newText, CefQueryCallback callback) {
        try {
            EditorService editorService = (EditorService)project.getService(EditorService.class);
            Editor editor = editorService.getActiveEditor();
            if (editor == null) {
                editor = ((EditorComponentImpl)editorService.getActiveEditorWindow().getManager().getSelectedEditor().getPreferredFocusedComponent()).getEditor();
            }

            if (editor == null || editorService.getActiveEditorWindow().getSelectedFile() == null) {
                callback.failure(513, "Open a file in editor to perform Insert code");
                return;
            }

            Document document = ((Editor)editor).getDocument();
            Caret primaryCaret = ((Editor)editor).getCaretModel().getPrimaryCaret();
            WriteCommandAction.runWriteCommandAction(project, () -> {
                document.replaceString(start, end, newText);
            });
            primaryCaret.removeSelection();
            primaryCaret.setSelection(start, start + newText.length());
        } catch (Exception var10) {
            var10.printStackTrace();
            callback.failure(500, var10.getMessage());
        }

    }
}
