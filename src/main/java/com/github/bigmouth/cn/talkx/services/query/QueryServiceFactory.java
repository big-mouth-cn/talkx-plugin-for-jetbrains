package com.github.bigmouth.cn.talkx.services.query;

import com.intellij.openapi.components.Service;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author allen
 * @date 2023/6/13
 * @since 1.0.0
 */
@Service
public final class QueryServiceFactory {

    private static final List<QueryService> PLUGIN_SERVICES = Lists.newArrayList();
    private final Map<String, QueryService> map = new ConcurrentHashMap<>();

    static {
        PLUGIN_SERVICES.add(new OpenExternalWebPageQueryService());
        PLUGIN_SERVICES.add(new ExecuteCodeTemplateQueryService());
        PLUGIN_SERVICES.add(new InsertCodeQueryService());
        PLUGIN_SERVICES.add(new OpenAiResponseInDiffViewQueryService());
        PLUGIN_SERVICES.add(new QueryEditorInfoQueryService());
        PLUGIN_SERVICES.add(new WriteToFileQueryService());
        PLUGIN_SERVICES.add(new ReadFromFileQueryService());
        PLUGIN_SERVICES.add(new UpdateIsAvailableQueryService());
        PLUGIN_SERVICES.add(new UpdateIsRequiredQueryService());
    }

    public QueryServiceFactory() {
        for (QueryService service : PLUGIN_SERVICES) {
            map.put(service.key(), service);
        }
    }

    public String invoke(QueryInvokeArgument queryInvokeArgument) throws Exception {
        String key = queryInvokeArgument.getKey();
        QueryService service = map.get(key);
        if (Objects.isNull(service)) {
            throw new NullPointerException("Not found service: " + key);
        }
        return service.invoke(queryInvokeArgument);
    }
}
