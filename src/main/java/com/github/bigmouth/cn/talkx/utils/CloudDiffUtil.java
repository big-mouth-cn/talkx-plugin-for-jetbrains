package com.github.bigmouth.cn.talkx.utils;

import com.github.bigmouth.cn.talkx.bean.FileInfo;
import com.github.bigmouth.cn.talkx.services.EditorService;
import com.github.bigmouth.cn.talkx.services.GitUtilService;
import com.intellij.diff.chains.DiffRequestProducer;
import com.intellij.diff.chains.SimpleDiffRequestChain;
import com.intellij.diff.contents.FileDocumentContentImpl;
import com.intellij.diff.editor.ChainDiffVirtualFile;
import com.intellij.diff.impl.CacheDiffRequestChainProcessor;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.openapi.fileEditor.impl.EditorWithProviderComposite;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import java.io.File;

public class CloudDiffUtil {
    public static Key<String> DIFF_ID = Key.create("Diff.DIFF_ID");
    public static Key<String> DIFF_FILENAME = Key.create("Diff.DIFF_FILENAME");
    public static Key<String> DIFF_FILEPATH_LEFT = Key.create("Diff.DIFF_FILEPATH_LEFT");
    public static Key<String> DIFF_FILEPATH_RIGHT = Key.create("Diff.DIFF_FILEPATH_RIGHT");
    public static Key<String> DIFF_OPTION_LEFT = Key.create("Diff.DIFF_OPTION_LEFT");
    public static Key<String> DIFF_OPTION_RIGHT = Key.create("Diff.DIFF_OPTION_RIGHT");
    public static Key<String> DIFF_FILE_UNIQUE_ID = Key.create("Diff.DIFF_FILE_UNIQUE_ID");
    public static Key<String> DIFF_AI_COORDINATES = Key.create("Diff.DIFF_AI_COORDINATES");

    public CloudDiffUtil() {
    }

    public static void closeIfDiffExsistForArtifact(Project project, String artifactId) {
        EditorService editorService = (EditorService)project.getService(EditorService.class);
        if (editorService.getActiveEditorWindow() != null && editorService.getActiveEditorWindow().getEditors() != null) {
            EditorWithProviderComposite[] var3 = editorService.getActiveEditorWindow().getEditors();
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                EditorWithProviderComposite editor = var3[var5];

                try {
                    if ("Diff".equals(editor.getFile().getName()) && editor.getFile() instanceof ChainDiffVirtualFile) {
                        SimpleDiffRequest simpleDiffRequest = (SimpleDiffRequest)((SimpleDiffRequestChain.DiffRequestProducerWrapper)((CacheDiffRequestChainProcessor)((ChainDiffVirtualFile)editor.getFile()).createProcessor(project)).getRequestChain().getRequests().get(0)).getRequest();
                        if (artifactId.equals(simpleDiffRequest.getUserData(DIFF_ID))) {
                            editor.dispose();
                        }
                    }
                } catch (Exception var8) {
                }
            }

        }
    }

    public static boolean setFocusIFDiffViewOpen(Project project, VirtualFile leftFile, VirtualFile rightFile) {
        EditorService editorService = (EditorService)project.getService(EditorService.class);
        if (editorService.getActiveEditorWindow() != null && editorService.getActiveEditorWindow().getEditors() != null) {
            EditorWithProviderComposite[] var4 = editorService.getActiveEditorWindow().getEditors();
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                EditorWithProviderComposite editor = var4[var6];

                try {
                    if ("Diff".equals(editor.getFile().getName()) && editor.getFile() instanceof ChainDiffVirtualFile) {
                        SimpleDiffRequest simpleDiffRequest = (SimpleDiffRequest)((SimpleDiffRequestChain.DiffRequestProducerWrapper)((CacheDiffRequestChainProcessor)((ChainDiffVirtualFile)editor.getFile()).createProcessor(project)).getRequestChain().getRequests().get(0)).getRequest();
                        String path1 = ((FileDocumentContentImpl)simpleDiffRequest.getContents().get(0)).getFile().getPath();
                        String path2 = ((FileDocumentContentImpl)simpleDiffRequest.getContents().get(1)).getFile().getPath();
                        if (leftFile.getPath().equals(path1) && rightFile.getPath().equals(path2)) {
                            editorService.getActiveEditorWindow().setEditor(editor, true);
                            return true;
                        }
                    }
                } catch (Exception var11) {
                    var11.printStackTrace();
                }
            }

            return false;
        } else {
            return false;
        }
    }

    public static FileInfo getCloudFileFromDiff(ChainDiffVirtualFile diffVirtualFile, Project project) {
        try {
            SimpleDiffRequest simpleDiffRequest = (SimpleDiffRequest)((SimpleDiffRequestChain.DiffRequestProducerWrapper)((CacheDiffRequestChainProcessor)diffVirtualFile.createProcessor(project)).getRequestChain().getRequests().get(0)).getRequest();
            VirtualFile fileLeft = ((FileDocumentContentImpl)simpleDiffRequest.getContents().get(0)).getFile();
            if (GenericUtils.isCloudFile(fileLeft)) {
                return new FileInfo(fileLeft, true);
            } else {
                VirtualFile fileRight = ((FileDocumentContentImpl)simpleDiffRequest.getContents().get(1)).getFile();
                if (GenericUtils.isCloudFile(fileRight)) {
                    return new FileInfo(fileRight, true);
                } else {
                    GitUtilService gitUtilService = (GitUtilService)project.getService(GitUtilService.class);
                    String defaultBaseDir = System.getProperty("java.io.tmpdir");
                    defaultBaseDir = defaultBaseDir.replace("\\", "/");
                    if (fileLeft.getPath().startsWith(defaultBaseDir)) {
                        return new FileInfo(fileRight, false);
                    } else {
                        return fileRight.getPath().startsWith(defaultBaseDir) ? new FileInfo(fileLeft, false) : new FileInfo(fileRight, false);
                    }
                }
            }
        } catch (ClassCastException var7) {
            var7.printStackTrace();
            String filePath = ((DiffRequestProducer)((CacheDiffRequestChainProcessor)diffVirtualFile.createProcessor(project)).getRequestChain().getRequests().get(0)).getName();
            File file = new File(filePath);
            VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(file);
            return new FileInfo(virtualFile, true);
        } catch (Exception var8) {
            var8.printStackTrace();
            return null;
        }
    }

    public static String getInfoFromDiffView(Project project, VirtualFile file, EditorService editorService, Key<String> diffKey) {
        EditorWithProviderComposite editor = editorService.getActiveEditorWindow().getSelectedEditor();

        try {
            if ("Diff".equals(editor.getFile().getName()) && editor.getFile() instanceof ChainDiffVirtualFile) {
                SimpleDiffRequest simpleDiffRequest = (SimpleDiffRequest)((SimpleDiffRequestChain.DiffRequestProducerWrapper)((CacheDiffRequestChainProcessor)((ChainDiffVirtualFile)editor.getFile()).createProcessor(project)).getRequestChain().getRequests().get(0)).getRequest();
                if (file.getPath().equals(simpleDiffRequest.getUserData(DIFF_FILEPATH_LEFT)) || file.getPath().equals(simpleDiffRequest.getUserData(DIFF_FILEPATH_RIGHT))) {
                    return (String)simpleDiffRequest.getUserData(diffKey);
                }
            }

            return null;
        } catch (Exception var6) {
            return null;
        }
    }
}
