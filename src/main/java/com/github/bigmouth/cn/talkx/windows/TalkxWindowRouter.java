package com.github.bigmouth.cn.talkx.windows;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bigmouth.cn.talkx.services.query.QueryInvokeArgument;
import com.github.bigmouth.cn.talkx.services.query.QueryServiceFactory;
import com.github.bigmouth.cn.talkx.utils.CommunicationRequest;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.browser.CefMessageRouter;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefMessageRouterHandler;

import java.util.Objects;

/**
 * @author allen
 * @date 2023/6/1
 * @since 1.0.0
 */
public class TalkxWindowRouter {

    private static final Logger logger = Logger.getInstance(TalkxWindowRouter.class);

    public CefMessageRouter getCefMessageRouter(Project project) {
        CefMessageRouter.CefMessageRouterConfig config = new CefMessageRouter.CefMessageRouterConfig("sendDataToJava", "sendDataFailure");
        CefMessageRouter cefMessageRouter = CefMessageRouter.create(config);
        cefMessageRouter.addHandler(new CefMessageRouterHandler() {
            @Override
            public boolean onQuery(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent, CefQueryCallback callback) {
                System.out.println("onQuery: " + request);
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    CommunicationRequest communicationRequest = objectMapper.readValue(request, CommunicationRequest.class);
                    System.out.println(communicationRequest);
                    String key = communicationRequest.getKey();
                    Object data = communicationRequest.getData();

                    QueryInvokeArgument queryInvokeArgument = new QueryInvokeArgument()
                            .setProject(project)
                            .setKey(key)
                            .setData(objectMapper.writeValueAsString(data))
                            .setOnSuccess(callback::success)
                            .setOnFailure(e -> {
                                e.printStackTrace();
                                callback.failure(e.getCode(), e.getMessage());
                            });

                    String res = project.getService(QueryServiceFactory.class).invoke(queryInvokeArgument);
                    if (Objects.nonNull(res)) {
                        System.out.println("[TalkX] == Query: " + res);
                        callback.success(res);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.failure(500, e.getMessage());
                }
                return true;
            }

            @Override
            public void onQueryCanceled(CefBrowser browser, CefFrame frame, long queryId) {
                System.out.println("Cancel the query: " + queryId);
            }

            @Override
            public void setNativeRef(String identifer, long nativeRef) {
            }

            @Override
            public long getNativeRef(String identifer) {
                return 0L;
            }
        }, true);
        return cefMessageRouter;
    }
}
