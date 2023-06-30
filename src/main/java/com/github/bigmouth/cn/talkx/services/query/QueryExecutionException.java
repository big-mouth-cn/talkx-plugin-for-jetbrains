package com.github.bigmouth.cn.talkx.services.query;

/**
 * @author allen
 * @date 2023/6/13
 * @since 1.0.0
 */
public class QueryExecutionException extends RuntimeException {

    private final int code;

    public QueryExecutionException(int code) {
        this.code = code;
    }

    public QueryExecutionException(String message, int code) {
        super(message);
        this.code = code;
    }

    public QueryExecutionException(String message, Throwable cause, int code) {
        super(message, cause);
        this.code = code;
    }

    public QueryExecutionException(Throwable cause, int code) {
        super(cause);
        this.code = code;
    }

    public QueryExecutionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, int code) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
