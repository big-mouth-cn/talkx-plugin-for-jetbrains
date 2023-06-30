package com.github.bigmouth.cn.talkx.utils;

import com.google.gson.Gson;

import java.util.Objects;

/**
 * @author allen
 * @date 2023/6/1
 * @since 1.0.0
 */
public class CommunicationRequest {

    private String key;
    private Object data;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CommunicationRequest{" +
                "key='" + key + '\'' +
                ", data=" + data +
                '}';
    }
}
