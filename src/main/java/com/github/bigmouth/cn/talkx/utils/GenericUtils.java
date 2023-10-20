package com.github.bigmouth.cn.talkx.utils;

import com.github.bigmouth.cn.talkx.services.EditorService;
import com.github.bigmouth.cn.talkx.setting.Constant;
import com.google.gson.JsonObject;
import com.intellij.diff.editor.ChainDiffVirtualFile;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ui.UIUtil;
import com.intellij.util.ui.UIUtil.FontSize;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author allen
 * @date 2023/6/13
 * @since 1.0.0
 */
public class GenericUtils {
    public GenericUtils() {
    }

    public static String urlEncode(String str) {
        try {
            return URLEncoder.encode(str, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return str;
        }
    }

    public static String getMD5(String filePath) {
        File file = new File(filePath);
        String md5 = null;
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(file);
            md5 = DigestUtils.md5Hex(IOUtils.toByteArray(fileInputStream));
            fileInputStream.close();
            return md5;
        } catch (IOException var5) {
            var5.printStackTrace();
            return "error";
        }
    }

    public static String[] getClodFileMeta(String vfFilePath) {
        File file = new File(vfFilePath);
        return file.exists() ? file.getParentFile().getName().split("_") : new String[]{"vfFilePath"};
    }

    public static boolean isCloudFile(VirtualFile vf) {
        String[] paramValues = getClodFileMeta(vf.getPath());
        return paramValues != null && paramValues.length == 6;
    }

    public static boolean getCloudFileData(Project project, JsonObject data, VirtualFile vf) {
        String[] paramValues = getClodFileMeta(vf.getPath());
        boolean returnValue = false;
        if (data == null) {
            data = new JsonObject();
        }

        if (paramValues != null && paramValues.length == 6) {
            data.addProperty("cloudFile", "y");
            data.addProperty("assetId", paramValues[0]);
            data.addProperty("cid", paramValues[1]);
            data.addProperty("artifactSourceId", paramValues[1]);
            data.addProperty("discussionId", paramValues[2]);
            data.addProperty("wid", paramValues[3]);
            data.addProperty("wgid", paramValues[4]);
            data.addProperty("artifactId", paramValues[5]);
            returnValue = true;
        } else {
            data.addProperty("cloudFile", "n");
            returnValue = false;
        }

        try {
            EditorService editorService = (EditorService)project.getService(EditorService.class);
            if (editorService.getActiveEditorWindow().getSelectedFile() instanceof ChainDiffVirtualFile) {
                data.addProperty("isDiffView", "y");
            } else {
                data.addProperty("isDiffView", "n");
            }
        } catch (Exception var6) {
            var6.printStackTrace();
            data.addProperty("isDiffView", "n");
        }

        return returnValue;
    }

    public static String getVersionedFileName(String fileName, String fileVersion) {
        if (fileVersion == null) {
            return fileName;
        } else {
            String finalFileName = "";
            int lastIndexOf = fileName.lastIndexOf(".");
            if (lastIndexOf != -1) {
                String fileNameWithoutExtension = fileName.substring(0, lastIndexOf);
                String extension = fileName.substring(lastIndexOf + 1);
                finalFileName = fileNameWithoutExtension + "_" + fileVersion;
                finalFileName = finalFileName + "." + extension;
            } else {
                finalFileName = fileName + "_" + fileVersion;
            }

            return finalFileName;
        }
    }

    public static String getTempFileDirectoryName(HashMap requestMap) {
        String tempDirectoryName = null;
        return requestMap.get("id").toString() + "_" + requestMap.get("cid").toString() + "_" + requestMap.get("chid").toString() + "_" + requestMap.get("wid").toString() + "_" + requestMap.get("wgid").toString() + "_" + requestMap.get("artifactId").toString();
    }

    public static int generateRandomInt(int max, int min) {
        return Double.valueOf(Math.random() * (double)(max - min + 1) + (double)min).intValue();
    }

    public static Map createMapForInputParams(String tokenURL, String unInstallURL, String userLoginStatusURL, String navigatorInfo) {
        Map<String, String> map = new HashMap();
        map.put("tokenURL", tokenURL);
        map.put("unInstallURL", unInstallURL);
        map.put("userLoginStatusURL", userLoginStatusURL);
        map.put("navigatorInfo", navigatorInfo);
        return map;
    }

    public static String getJetBrainsIDEInfo() {
        return Constant.IDE_VERSION;
    }

    public static String getJetBrainsParentIDEName() {
        return "JB";
    }

    public static String getTalkxVersion() {
        return Constant.TALKX_VERSION;
    }

    public static int getFontSize() {
        return UIUtil.getFont(FontSize.NORMAL, (Font)null).getSize();
    }

    public static String getFontFamily() {
        return UIUtil.getFont(FontSize.NORMAL, (Font)null).getFontName();
    }

    public static String getTheme() {
        String theme = "light";
        if (UIUtil.isUnderDarcula()) {
            theme = "dark";
        }

        return theme;
    }

    public static String getEnvironment() {
        try {
            return "PROD".equals("staging") ? "staging" : "PROD";
        } catch (Exception var1) {
            var1.printStackTrace();
            return "staging";
        }
    }
}
