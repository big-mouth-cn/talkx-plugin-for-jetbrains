package com.github.bigmouth.cn.talkx.handlers;

import org.cef.callback.CefCallback;
import org.cef.misc.IntRef;
import org.cef.misc.StringRef;
import org.cef.network.CefResponse;

import java.io.IOException;

/**
 * @author allen
 * @date 2023/6/1
 * @since 1.0.0
 */
public class ClosedConnection implements ResourceHandlerState {

    @Override
    public void getResponseHeaders(CefResponse cefResponse, IntRef responseLength, StringRef redirectUrl) {
        cefResponse.setStatus(404);
    }

    @Override
    public Boolean readResponse(byte[] dataOut, int designedBytesToRead, IntRef bytesRead, CefCallback callback) throws IOException {
        return false;
    }

    @Override
    public void close() throws IOException {
    }
}
