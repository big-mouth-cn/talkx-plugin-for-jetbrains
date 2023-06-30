package com.github.bigmouth.cn.talkx.services.query;

import com.github.bigmouth.cn.talkx.bean.FileInfo;
import com.github.bigmouth.cn.talkx.services.EditorHelper;
import com.github.bigmouth.cn.talkx.services.EditorService;
import com.github.bigmouth.cn.talkx.services.TalkxWindowService;
import com.github.bigmouth.cn.talkx.utils.CloudDiffUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.intellij.diff.editor.ChainDiffVirtualFile;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.impl.EditorComponentImpl;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.impl.EditorWindow;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindowManager;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author allen
 * @date 2023/6/13
 * @since 1.0.0
 */
public class ExecuteCodeTemplateQueryService extends AbstractInvokeLaterQueryService implements QueryService {

    @Override
    public String key() {
        return "execute-code-template";
    }

    @Override
    protected void invokeOnLater(QueryInvokeArgument queryInvokeArgument) {
        Project project = queryInvokeArgument.getProject();
        String data = queryInvokeArgument.getData();
        Consumer<String> onSuccess = queryInvokeArgument.getOnSuccess();
        Consumer<QueryExecutionException> onFailure = queryInvokeArgument.getOnFailure();
        if (project.isInitialized() && !project.isDisposed()) {
            EditorService editorService = project.getService(EditorService.class);
            EditorWindow activeEditorWindow = editorService.getActiveEditorWindow();
            VirtualFile activeVirtualFile = editorService.getActiveVirtualFile();
            if (activeEditorWindow == null) {
                editorService.setDefault();
            }

            FileEditor selectedEditor = null;
            boolean valid = Objects.nonNull(activeEditorWindow) && Objects.nonNull(activeVirtualFile) && Objects.nonNull(selectedEditor = activeEditorWindow.getManager().getSelectedEditor());
            if (!valid) {
                onFailure.accept(new QueryExecutionException("请先选择一些代码，然后点击快捷键以获取所需的输出。", 400));
                return;
            }

            EditorImpl editor = ((EditorComponentImpl) selectedEditor.getPreferredFocusedComponent()).getEditor();
            JsonObject jsonObject = EditorHelper.getFileSelectionDetails(editor, true);
            VirtualFile fileRef = null;

            try {
                if (editorService.getActiveEditorWindow().getSelectedFile() instanceof ChainDiffVirtualFile) {
                    FileInfo fileInfo = CloudDiffUtil.getCloudFileFromDiff((ChainDiffVirtualFile)editorService.getActiveVirtualFile(), project);
                    if (fileInfo != null) {
                        String leftFile = CloudDiffUtil.getInfoFromDiffView(project, fileInfo.getVf(), editorService, CloudDiffUtil.DIFF_FILEPATH_LEFT);
                        String rightFile = CloudDiffUtil.getInfoFromDiffView(project, fileInfo.getVf(), editorService, CloudDiffUtil.DIFF_FILEPATH_RIGHT);
                        if (editor.getDocument().toString().indexOf(leftFile) > -1) {
                            fileRef = LocalFileSystem.getInstance().refreshAndFindFileByPath(leftFile);
                        } else if (editor.getDocument().toString().indexOf(rightFile) > -1) {
                            fileRef = LocalFileSystem.getInstance().refreshAndFindFileByPath(rightFile);
                        } else {
                            fileRef = LocalFileSystem.getInstance().refreshAndFindFileByPath(activeVirtualFile.getCanonicalPath());
                        }
                    } else {
                        fileRef = LocalFileSystem.getInstance().refreshAndFindFileByPath(activeVirtualFile.getCanonicalPath());
                    }
                } else {
                    fileRef = activeVirtualFile;
                }

                String name = fileRef.getName();
                String canonicalPath = fileRef.getCanonicalPath();

                ExecuteCodeTemplate executeCodeTemplate = new Gson().fromJson(data, ExecuteCodeTemplate.class);
                ApplicationManager.getApplication().invokeAndWait(() -> {
                    ToolWindowManager windowManager = ToolWindowManager.getInstance(project);
                    String actionKey = executeCodeTemplate.getExecuteTemplateKey();
                    windowManager.getToolWindow("TalkX").activate(() -> {
                        System.out.println(actionKey + " Enable TalkX window.");
                    }, true, true);
                    jsonObject.addProperty("cloudFile", "n");
                    jsonObject.addProperty("isDiffView", "n");
                    jsonObject.addProperty("fileName", name);
                    jsonObject.addProperty("filePath", canonicalPath);
                    jsonObject.addProperty("isGitRepository", false);
                    jsonObject.addProperty("initialGitFileStatus", "local");
                    jsonObject.addProperty("location", "talkx");

                    JsonObject result = new JsonObject();
                    result.addProperty("key", actionKey);
                    result.addProperty("data", jsonObject.toString());
                    System.out.println(actionKey + " == " + result);

                    boolean userSelectedText = jsonObject.get("userSelectedText").getAsBoolean();
                    if (!userSelectedText) {
                        onFailure.accept(new QueryExecutionException("请先选择一些代码，然后点击快捷键以获取所需的输出。", 400));
                        return;
                    }

                    project.getService(TalkxWindowService.class).notifyIdeAppInstance(result);
                });

                onSuccess.accept("success");
            } catch (Exception e) {
                onFailure.accept(new QueryExecutionException(e.getMessage(), 500));
            }
        }
    }

    public static class ExecuteCodeTemplate {
        private String executeTemplateKey;

        public String getExecuteTemplateKey() {
            return executeTemplateKey;
        }

        public void setExecuteTemplateKey(String executeTemplateKey) {
            this.executeTemplateKey = executeTemplateKey;
        }
    }
}
