package com.github.bigmouth.cn.talkx.services.query;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;

/**
 * @author allen
 * @date 2023/6/15
 * @since 1.0.0
 */
public abstract class AbstractInvokeLaterQueryService implements QueryService {

    /**
     * invokeLater
     * @param queryInvokeArgument argument
     */
    protected abstract void invokeOnLater(QueryInvokeArgument queryInvokeArgument);

    @Override
    public String invoke(QueryInvokeArgument queryInvokeArgument) throws Exception {
        ApplicationManager.getApplication().invokeLater(() -> {
            invokeOnLater(queryInvokeArgument);
        }, ModalityState.NON_MODAL);
        return null;
    }
}
