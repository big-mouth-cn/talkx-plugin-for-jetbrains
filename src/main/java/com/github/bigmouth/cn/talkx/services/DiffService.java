package com.github.bigmouth.cn.talkx.services;

import com.github.bigmouth.cn.talkx.utils.CloudDiffUtil;
import com.github.bigmouth.cn.talkx.utils.GenericUtils;
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
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.impl.EditorComponentImpl;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.fileEditor.ex.FileEditorWithProvider;
import com.intellij.openapi.fileEditor.impl.EditorWithProviderComposite;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.cef.callback.CefQueryCallback;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class DiffService {
    private static final Logger LOG = Logger.getInstance(DiffService.class);
    private static DiffManager diffManagerInstance = DiffManager.getInstance();

    public DiffService() {
    }

    public void openDiffViewForBitoAI(@NotNull Project project, HashMap requestMap, CefQueryCallback callback) {
        if (project == null) {
            return;
        }

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
                callback.failure(500, "File doesn't exists in user's local");
            }

            String defaultBaseDir = System.getProperty("java.io.tmpdir");
            String tempDirectoryName = "bitoAiDiff";
            Path path = Paths.get(defaultBaseDir + File.separator + tempDirectoryName);
            Path tempFile = Files.createDirectories(path);
            String tempFilePath = defaultBaseDir + File.separator + tempDirectoryName + File.separator + GenericUtils.getVersionedFileName(fileName, String.valueOf(GenericUtils.generateRandomInt(0, 10000)));
            File leftFileTemp = new File(tempFilePath);
            leftFileTemp = fileService.copyFileContentForWrite(new File(filePath), leftFileTemp);
            leftFileTemp = FileService.replaceContentInFile(leftFileTemp, selectedText, newText, Integer.parseInt(startLine), Integer.parseInt(endLine));
            leftFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(leftFileTemp.getCanonicalPath());
            DiffContent content1 = DiffContentFactory.getInstance().create(project, leftFile);
            DiffContent content2 = DiffContentFactory.getInstance().create(project, rightFile);
            leftFileTitle = leftFile.getName() + "(Bito suggested code)";
            rightFileTitle = rightFile.getName() + "(Original)";
            String diffTitle = "Bito AI: Diff view";
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
            callback.success(jsonReturnObj.toString());
        } catch (Exception var37) {
            var37.printStackTrace();
            System.out.println(var37.getMessage());
        }

    }

    public void acceptDiffView(@NotNull Project project, HashMap requestMap, CefQueryCallback callback) {
        if (project == null) {
            return;
        }

        try {
            System.out.println("acceptDiffView is triggered");
            String var10000;
            if (requestMap.get("fileName") != null) {
                requestMap.get("fileName").toString();
            } else {
                var10000 = "";
            }

            if (requestMap.get("filePath") != null) {
                requestMap.get("filePath").toString();
            } else {
                var10000 = "";
            }

            if (requestMap.get("selectedText") != null) {
                requestMap.get("selectedText").toString();
            } else {
                var10000 = "";
            }

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
            EditorService editorService = (EditorService)project.getService(EditorService.class);
            editorService.getActiveEditorWindow().getEditors();
            if (editorService.getActiveEditorWindow() == null || editorService.getActiveEditorWindow().getEditors() == null) {
                callback.failure(500, "Open a file in editor to Accept changes");
            }

            boolean matchFound = false;
            EditorWithProviderComposite[] var21 = editorService.getActiveEditorWindow().getEditors();
            int var22 = var21.length;

            for(int var23 = 0; var23 < var22; ++var23) {
                EditorWithProviderComposite editor = var21[var23];

                try {
                    if ("Diff".equals(editor.getFile().getName()) && editor.getFile() instanceof ChainDiffVirtualFile) {
                        SimpleDiffRequest simpleDiffRequest = (SimpleDiffRequest)((SimpleDiffRequestChain.DiffRequestProducerWrapper)((CacheDiffRequestChainProcessor)((ChainDiffVirtualFile)editor.getFile()).createProcessor(project)).getRequestChain().getRequests().get(0)).getRequest();
                        if (uniqueDiffId.equals(simpleDiffRequest.getUserData(CloudDiffUtil.DIFF_FILE_UNIQUE_ID))) {
                            matchFound = true;
                            editor.dispose();
                            File tobeDeleted = new File((String)simpleDiffRequest.getUserData(CloudDiffUtil.DIFF_FILEPATH_RIGHT));
                            FileService.deleteFile(tobeDeleted);
                            VirtualFile vFile = LocalFileSystem.getInstance().refreshAndFindFileByPath((String)simpleDiffRequest.getUserData(CloudDiffUtil.DIFF_FILEPATH_LEFT));
                            FileEditorManagerEx manager = FileEditorManagerEx.getInstanceEx(project);
                            FileEditorWithProvider previousSelection = manager.getSelectedEditorWithProvider(vFile);
                            editor.getFileEditorManager().setSelectedEditor(vFile, previousSelection.getProvider().getEditorTypeId());
                            Editor editor1 = ((EditorComponentImpl)editorService.getActiveEditorWindow().getManager().getSelectedEditor().getPreferredFocusedComponent()).getEditor();
                            int startLineInt = Integer.parseInt(startLine);
                            --startLineInt;
                            int endLineInt = Integer.parseInt(endLine);
                            --endLineInt;
                            int startOffset = editor1.logicalPositionToOffset(new LogicalPosition(startLineInt, Integer.parseInt(startColumn)));
                            int endOffset = editor1.logicalPositionToOffset(new LogicalPosition(endLineInt, Integer.parseInt(endColumn)));
                            EditorService.insertCodeInEditor(project, editor.getFile(), startOffset, endOffset, newText, callback);
                            break;
                        }
                    }
                } catch (Exception var35) {
                    callback.failure(500, "Something went wrong while Accept changes");
                }
            }

            if (matchFound) {
                JsonObject jsonReturnObj = new JsonObject();
                jsonReturnObj.addProperty("uniqueDiffId", uniqueDiffId);
                callback.success(jsonReturnObj.toString());
            } else {
                callback.failure(513, "No Diff file opened in editor to perform Accept changes");
            }
        } catch (Exception var36) {
            var36.printStackTrace();
            System.out.println(var36.getMessage());
            callback.failure(500, "Something went wrong while Accept changes");
        }

    }

    public void rejectDiffView(@NotNull Project project, HashMap requestMap, CefQueryCallback callback) {
        if (project == null) {
            return;
        }

        try {
            System.out.println("rejectDiffView is triggered");
            String uniqueDiffId = requestMap.get("uniqueDiffId") != null ? requestMap.get("uniqueDiffId").toString() : "";
            EditorService editorService = (EditorService)project.getService(EditorService.class);
            if (editorService.getActiveEditorWindow() == null || editorService.getActiveEditorWindow().getEditors() == null) {
                callback.failure(500, "Open a file in editor to Reject changes");
            }

            boolean matchFound = false;
            EditorWithProviderComposite[] var7 = editorService.getActiveEditorWindow().getEditors();
            int var8 = var7.length;

            for(int var9 = 0; var9 < var8; ++var9) {
                EditorWithProviderComposite editor = var7[var9];

                try {
                    if ("Diff".equals(editor.getFile().getName()) && editor.getFile() instanceof ChainDiffVirtualFile) {
                        SimpleDiffRequest simpleDiffRequest = (SimpleDiffRequest)((SimpleDiffRequestChain.DiffRequestProducerWrapper)((CacheDiffRequestChainProcessor)((ChainDiffVirtualFile)editor.getFile()).createProcessor(project)).getRequestChain().getRequests().get(0)).getRequest();
                        if (uniqueDiffId.equals(simpleDiffRequest.getUserData(CloudDiffUtil.DIFF_FILE_UNIQUE_ID))) {
                            matchFound = true;
                            editor.dispose();
                            File tobeDeleted = new File((String)simpleDiffRequest.getUserData(CloudDiffUtil.DIFF_FILEPATH_RIGHT));
                            FileService.deleteFile(tobeDeleted);
                            break;
                        }
                    }
                } catch (Exception var13) {
                    callback.failure(500, "Something went wrong while Reject changes");
                }
            }

            if (matchFound) {
                JsonObject jsonReturnObj = new JsonObject();
                jsonReturnObj.addProperty("uniqueDiffId", uniqueDiffId);
                callback.success(jsonReturnObj.toString());
            } else {
                callback.failure(513, "No Diff file opened in editor to perform Reject changes");
            }
        } catch (Exception var14) {
            var14.printStackTrace();
            System.out.println(var14.getMessage());
            callback.failure(500, "Something went wrong while Reject changes");
        }

    }

    public void closeDiffViewIfAlreadyOpened(@NotNull Project project, String uniqueDiffId) {
        if (project == null) {
            return;
        }

        EditorService editorService = project.getService(EditorService.class);
        EditorWithProviderComposite[] var4 = editorService.getActiveEditorWindow().getEditors();
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
