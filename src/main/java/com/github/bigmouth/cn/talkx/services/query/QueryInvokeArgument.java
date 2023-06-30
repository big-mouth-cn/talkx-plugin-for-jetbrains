package com.github.bigmouth.cn.talkx.services.query;

import com.intellij.openapi.project.Project;

import java.util.function.Consumer;

/**
 * @author allen
 * @date 2023/6/14
 * @since 1.0.0
 */
public class QueryInvokeArgument {

    private Project project;
    private String key;
    private String data;
    private Consumer<String> onSuccess;
    private Consumer<QueryExecutionException> onFailure;

    public QueryInvokeArgument setProject(Project project) {
        this.project = project;
        return this;
    }

    public QueryInvokeArgument setKey(String key) {
        this.key = key;
        return this;
    }

    public QueryInvokeArgument setData(String data) {
        this.data = data;
        return this;
    }

    public QueryInvokeArgument setOnSuccess(Consumer<String> onSuccess) {
        this.onSuccess = onSuccess;
        return this;
    }

    public QueryInvokeArgument setOnFailure(Consumer<QueryExecutionException> onFailure) {
        this.onFailure = onFailure;
        return this;
    }

    public Project getProject() {
        return project;
    }

    public String getKey() {
        return key;
    }

    public String getData() {
        return data;
    }

    public Consumer<String> getOnSuccess() {
        return onSuccess;
    }

    public Consumer<QueryExecutionException> getOnFailure() {
        return onFailure;
    }
}
