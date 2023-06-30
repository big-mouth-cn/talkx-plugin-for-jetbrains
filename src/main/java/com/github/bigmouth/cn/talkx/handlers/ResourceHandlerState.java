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
public interface ResourceHandlerState {

    void getResponseHeaders(CefResponse var1, IntRef var2, StringRef var3);

    Boolean readResponse(byte[] var1, int var2, IntRef var3, CefCallback var4) throws IOException;

    void close() throws IOException;
}
