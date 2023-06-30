package com.github.bigmouth.cn.talkx.services.query;

/**
 * @author allen
 * @date 2023/6/13
 * @since 1.0.0
 */
public interface QueryService {

    /**
     * 这个查询服务属于哪个 key
     * @return 唯一的key，如：write-to-file
     */
    String key();

    /**
     * 执行
     * @param queryInvokeArgument 执行参数
     * @return 同步情况下，这个会返回成功状态下的内容。如果为{@code null}，表示异步处理。
     * @throws Exception 异常
     */
    String invoke(QueryInvokeArgument queryInvokeArgument) throws Exception;
}
