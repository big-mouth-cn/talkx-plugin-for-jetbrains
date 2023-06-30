package com.github.bigmouth.cn.talkx.services.query;

import com.github.bigmouth.cn.talkx.services.*;
import com.github.bigmouth.cn.talkx.utils.CloudDiffUtil;
import com.github.bigmouth.cn.talkx.utils.GenericUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.intellij.diff.DiffContentFactory;
import com.intellij.diff.DiffDialogHints;
import com.intellij.diff.DiffManager;
import com.intellij.diff.chains.SimpleDiffRequestChain;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.editor.ChainDiffVirtualFile;
import com.intellij.diff.impl.CacheDiffRequestChainProcessor;
import com.intellij.diff.requests.DiffRequest;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.diff.util.DiffUserDataKeys;
import com.intellij.diff.util.Side;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.fileEditor.impl.EditorWindow;
import com.intellij.openapi.fileEditor.impl.EditorWithProviderComposite;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Objects;

/**
 * @author allen
 * @date 2023/6/14
 * @since 1.0.0
 */
public class OpenAiResponseInDiffViewQueryService implements QueryService {

    private static DiffManager diffManagerInstance = DiffManager.getInstance();

    @Override
    public String key() {
        return "open-ai-response-in-diff-view";
    }

    @Override
    public String invoke(QueryInvokeArgument queryInvokeArgument) throws Exception {
        ApplicationManager.getApplication().invokeLater(() -> {
            Project project = queryInvokeArgument.getProject();
            String data = queryInvokeArgument.getData();
            HashMap requestMap = new Gson().fromJson(data, HashMap.class);

            DiffRequest request = null;

            try {
                VirtualFile rightFile = null;
                VirtualFile leftFile = null;
                String leftFileTitle = "";
                String rightFileTitle = "";
                FileService fileService = new FileService();
                EditorService editorService = (EditorService)project.getService(EditorService.class);
                String fileName = requestMap.get("fileName") != null ? requestMap.get("fileName").toString() : "";
                String filePath = requestMap.get("filePath") != null ? requestMap.get("filePath").toString() : "";
                String selectedText = requestMap.get("selectedText") != null ? requestMap.get("selectedText").toString() : "";
                String newText = requestMap.get("newText") != null ? requestMap.get("newText").toString() : "";
                String uniqueDiffId = requestMap.get("uniqueDiffId") != null ? requestMap.get("uniqueDiffId").toString() : "";
                String selectionCordinates = requestMap.get("selectionCordinates") != null ? requestMap.get("selectionCordinates").toString() : "{}";
                JSONObject jsonObject = new JSONObject(selectionCordinates);
                String jsonObjectStartString = jsonObject.getString("start");
                String jsonObjectEndString = jsonObject.getString("end");
                JSONObject jsonObjectStart = new JSONObject(jsonObjectStartString);
                String startLine = jsonObjectStart.getString("line");
                String startColumn = jsonObjectStart.getString("column");
                JSONObject jsonObjectEnd = new JSONObject(jsonObjectEndString);
                String endLine = jsonObjectEnd.getString("line");
                String endColumn = jsonObjectEnd.getString("column");
                rightFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(filePath);
                if (rightFile == null) {
                    queryInvokeArgument.getOnFailure().accept(new QueryExecutionException("本地不存在这个文件了，无法进行比较。", 500));
                    return;
                }

                String defaultBaseDir = System.getProperty("java.io.tmpdir");
                String tempDirectoryName = "talkxAiDiff";
                Path path = Paths.get(defaultBaseDir + File.separator + tempDirectoryName);
                Path tempFile = Files.createDirectories(path);
                String tempFilePath = defaultBaseDir + File.separator + tempDirectoryName + File.separator + GenericUtils.getVersionedFileName(fileName, String.valueOf(GenericUtils.generateRandomInt(0, 10000)));
                File leftFileTemp = new File(tempFilePath);
                leftFileTemp = fileService.copyFileContentForWrite(new File(filePath), leftFileTemp);
                leftFileTemp = FileService.replaceContentInFile(leftFileTemp, selectedText, newText, Integer.parseInt(startLine), Integer.parseInt(endLine));
                leftFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(leftFileTemp.getCanonicalPath());
                DiffContent content1 = DiffContentFactory.getInstance().create(project, leftFile);
                DiffContent content2 = DiffContentFactory.getInstance().create(project, rightFile);
                leftFileTitle = leftFile.getName() + "(TalkX suggested code)";
                rightFileTitle = rightFile.getName() + "(Original)";
                String diffTitle = "TalkX: Diff view";
                request = new SimpleDiffRequest(diffTitle, content1, content2, leftFileTitle.toString(), rightFileTitle.toString());
                request.putUserData(DiffUserDataKeys.SCROLL_TO_LINE, Pair.create(Side.RIGHT, Integer.parseInt(startLine)));
                request.putUserData(DiffUserDataKeys.PREFERRED_FOCUS_SIDE, Side.RIGHT);
                request.putUserData(CloudDiffUtil.DIFF_FILENAME, fileName);
                request.putUserData(CloudDiffUtil.DIFF_FILEPATH_LEFT, leftFile.getPath());
                request.putUserData(CloudDiffUtil.DIFF_FILEPATH_RIGHT, rightFile.getPath());
                request.putUserData(CloudDiffUtil.DIFF_FILE_UNIQUE_ID, uniqueDiffId);
                request.putUserData(CloudDiffUtil.DIFF_AI_COORDINATES, selectionCordinates);
                this.closeDiffViewIfAlreadyOpened(project, uniqueDiffId);
                diffManagerInstance.showDiff(project, request, DiffDialogHints.DEFAULT);
                JsonObject jsonReturnObj = new JsonObject();
                jsonReturnObj.addProperty("uniqueDiffId", uniqueDiffId);
                queryInvokeArgument.getOnSuccess().accept(jsonReturnObj.toString());
            } catch (Exception var37) {
                queryInvokeArgument.getOnFailure().accept(new QueryExecutionException(var37.getMessage(), 500));
            }
        }, ModalityState.NON_MODAL);

        return null;
    }

    public void closeDiffViewIfAlreadyOpened(@NotNull Project project, String uniqueDiffId) {
        if (project == null) {
            return;
        }

        EditorService editorService = (EditorService)project.getService(EditorService.class);
        EditorWindow activeEditorWindow = editorService.getActiveEditorWindow();
        if (Objects.isNull(activeEditorWindow)) {
            return;
        }
        EditorWithProviderComposite[] var4 = activeEditorWindow.getEditors();
        int var5 = var4.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            EditorWithProviderComposite editor = var4[var6];

            try {
                if ("Diff".equals(editor.getFile().getName()) && editor.getFile() instanceof ChainDiffVirtualFile) {
                    SimpleDiffRequest simpleDiffRequest = (SimpleDiffRequest)((SimpleDiffRequestChain.DiffRequestProducerWrapper)((CacheDiffRequestChainProcessor)((ChainDiffVirtualFile)editor.getFile()).createProcessor(project)).getRequestChain().getRequests().get(0)).getRequest();
                    if (uniqueDiffId.equals(simpleDiffRequest.getUserData(CloudDiffUtil.DIFF_FILE_UNIQUE_ID))) {
                        editor.dispose();
                        break;
                    }
                }
            } catch (Exception var9) {
                var9.printStackTrace();
            }
        }

    }
}
