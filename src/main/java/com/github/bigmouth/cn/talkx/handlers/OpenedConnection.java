package com.github.bigmouth.cn.talkx.handlers;

import org.cef.callback.CefCallback;
import org.cef.handler.CefLoadHandler;
import org.cef.misc.IntRef;
import org.cef.misc.StringRef;
import org.cef.network.CefResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

/**
 * @author allen
 * @date 2023/6/1
 * @since 1.0.0
 */
public class OpenedConnection  implements ResourceHandlerState {
    private InputStream inputStream;
    private final URLConnection connection;
    private boolean isConnectionOpened;

    public URLConnection connection() {
        return this.connection;
    }

    private InputStream inputStreamLazyCompute() throws IOException {
        try {
            if (!this.isConnectionOpened) {
                this.inputStream = this.connection().getInputStream();
                this.isConnectionOpened = true;
            }
        } catch (IOException var2) {
            throw var2;
        }

        return this.inputStream;
    }

    private InputStream inputStream() throws IOException {
        return !this.isConnectionOpened ? this.inputStreamLazyCompute() : this.inputStream;
    }

    @Override
    public void getResponseHeaders(CefResponse cefResponse, IntRef responseLength, StringRef redirectUrl) {
        try {
            String url = this.connection().getURL().toString();
            switch (url == null ? 0 : url.hashCode()) {
                default:
                    if (url.contains(".css")) {
                        cefResponse.setMimeType("text/css");
                    } else if (url.contains(".js")) {
                        cefResponse.setMimeType("text/javascript");
                    } else if (url.contains(".html")) {
                        cefResponse.setMimeType("text/html");
                    } else {
                        cefResponse.setMimeType(this.connection().getContentType());
                    }

                    responseLength.set(this.inputStream().available());
                    cefResponse.setStatus(200);
            }
        } catch (IOException var5) {
            cefResponse.setError(CefLoadHandler.ErrorCode.ERR_FILE_NOT_FOUND);
            cefResponse.setStatusText(var5.getLocalizedMessage());
            cefResponse.setStatus(404);
        }

    }

    @Override
    public Boolean readResponse(byte[] dataOut, int designedBytesToRead, IntRef bytesRead, CefCallback callback) throws IOException {
        int availableSize = this.inputStream().available();
        boolean isResponseRead;
        if (availableSize > 0) {
            int maxBytesToRead = Math.min(availableSize, designedBytesToRead);
            int realNumberOfReadBytes = this.inputStream().read(dataOut, 0, maxBytesToRead);
            bytesRead.set(realNumberOfReadBytes);
            isResponseRead = true;
        } else {
            this.inputStream().close();
            isResponseRead = false;
        }

        return isResponseRead;
    }

    @Override
    public void close() throws IOException {
        this.inputStream().close();
    }

    public OpenedConnection(URLConnection connection) {
        this.connection = connection;
    }
}

