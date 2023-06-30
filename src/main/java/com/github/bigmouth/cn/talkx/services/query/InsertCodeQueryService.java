package com.github.bigmouth.cn.talkx.services.query;

import com.github.bigmouth.cn.talkx.services.EditorService;
import com.google.gson.Gson;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.impl.EditorComponentImpl;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.impl.EditorWindow;
import com.intellij.openapi.project.Project;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author allen
 * @date 2023/6/14
 * @since 1.0.0
 */
public class InsertCodeQueryService extends AbstractInvokeLaterQueryService implements QueryService {
    @Override
    public String key() {
        return "insert-code";
    }

    @Override
    protected void invokeOnLater(QueryInvokeArgument queryInvokeArgument) {
        Project project = queryInvokeArgument.getProject();
        String data = queryInvokeArgument.getData();
        InsertCode insertCode = new Gson().fromJson(data, InsertCode.class);
        Consumer<QueryExecutionException> onFailure = queryInvokeArgument.getOnFailure();
        try {
            EditorService editorService = project.getService(EditorService.class);
            Editor editor = editorService.getActiveEditor();
            EditorWindow activeEditorWindow = editorService.getActiveEditorWindow();
            if (editor == null) {
                if (Objects.isNull(activeEditorWindow)) {
                    throw new QueryExecutionException("请打开编辑器中的文件，选择需要插入的位置。", 400);
                }
                FileEditor selectedEditor = activeEditorWindow.getManager().getSelectedEditor();
                editor = ((EditorComponentImpl) selectedEditor.getPreferredFocusedComponent()).getEditor();
            }

            if (editor == null || activeEditorWindow.getSelectedFile() == null) {
                throw new QueryExecutionException("请打开编辑器中的文件，选择需要插入的位置。", 400);
            } else {
                String text = insertCode.getCode();
                Document document = editor.getDocument();
                Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();
                int start = primaryCaret.getSelectionStart();
                int end = primaryCaret.getSelectionEnd();
                WriteCommandAction.runWriteCommandAction(project, () -> {
                    document.replaceString(start, end, text);
                });
                primaryCaret.removeSelection();
                primaryCaret.setSelection(start, start + text.length());
            }
            queryInvokeArgument.getOnSuccess().accept("success");
        } catch (QueryExecutionException e) {
            onFailure.accept(e);
        } catch (Exception e) {
            onFailure.accept(new QueryExecutionException(e.getMessage(), 500));
        }
    }

    public static class InsertCode {
        private String code;

        public String getCode() {
            return code;
        }

        public InsertCode setCode(String code) {
            this.code = code;
            return this;
        }
    }
}
