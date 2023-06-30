package com.github.bigmouth.cn.talkx.handlers;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.cef.callback.CefCallback;
import org.cef.handler.CefResourceHandler;
import org.cef.misc.IntRef;
import org.cef.misc.StringRef;
import org.cef.network.CefRequest;
import org.cef.network.CefResponse;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author allen
 * @date 2023/6/1
 * @since 1.0.0
 */
public class CustomResourceHandler implements CefResourceHandler {
    Logger logger;
    private ResourceHandlerState resourceHandlerState;
    private Project project;

    private ResourceHandlerState getResourceHandlerState() {
        return this.resourceHandlerState;
    }

    public CustomResourceHandler(@NotNull Project project) {
        if (project == null) {
        }

        this.logger = Logger.getInstance(CustomResourceHandler.class);
        this.project = project;
    }

    private void setResourceHandlerState(ResourceHandlerState resourceHandlerState) {
        this.resourceHandlerState = resourceHandlerState;
    }

    @Override
    public boolean processRequest(CefRequest cefRequest, CefCallback cefCallback) {
        boolean isRequestProcessed;
        try {
            String url = cefRequest.getURL();
            this.logger.info(url);
            System.out.println(url);
            boolean isDataPresent = false;
            String data = null;
            if (url.contains("http://ideapp/chat?data") || url.contains("http://ideapp/chat&appInfo")) {
                String decodedURL = URLDecoder.decode(url, "UTF-8");
                System.out.println(decodedURL);
                data = decodedURL != null && decodedURL.length() > 30 ? decodedURL.substring(decodedURL.indexOf("data={") + 5) : "";
                if (data.length() > 0) {
                    isDataPresent = true;
                }
            }

            System.out.println(url);
            String[] resourcePath = url.replace("http://ideapp/", "webview/").split(Pattern.quote("?"));
            System.out.println(Arrays.toString(resourcePath));
            String pathToResource = resourcePath.length > 0 ? resourcePath[0] : "webview/index.html";
            System.out.println(pathToResource);
            URL newUrl = this.getClass().getClassLoader().getResource(pathToResource);
            this.logger.debug("newUrl", new Object[]{newUrl});
            System.out.println(newUrl != null ? newUrl.toString() : null);
            URL destURL;
            if (newUrl != null) {
                if (resourcePath.length > 1) {
                    destURL = new URL(newUrl + "#/?" + resourcePath[1]);
                } else {
                    destURL = newUrl;
                }
            } else {
                destURL = this.getClass().getClassLoader().getResource("webview/index.html");
            }

            System.out.println(destURL != null ? destURL.toString() : null);
            OpenedConnection openedConnection = new OpenedConnection(((URL) Objects.requireNonNull(destURL)).openConnection());
            this.setResourceHandlerState(openedConnection);
            cefCallback.Continue();
            isRequestProcessed = true;
        } catch (IOException var12) {
            var12.printStackTrace();
            isRequestProcessed = false;
        }

        return isRequestProcessed;
    }

    @Override
    public void getResponseHeaders(CefResponse cefResponse, IntRef intRef, StringRef stringRef) {
        this.getResourceHandlerState().getResponseHeaders(cefResponse, intRef, stringRef);
    }

    @Override
    public boolean readResponse(byte[] dataOut, int designedBytesToRead, IntRef bytesRead, CefCallback callback) {
        boolean isResponseRead;
        try {
            isResponseRead = this.getResourceHandlerState().readResponse(dataOut, designedBytesToRead, bytesRead, callback);
        } catch (IOException var7) {
            var7.printStackTrace();
            isResponseRead = false;
        }

        return isResponseRead;
    }

    @Override
    public void cancel() {
        try {
            this.getResourceHandlerState().close();
            this.setResourceHandlerState((ResourceHandlerState)null);
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }
}
