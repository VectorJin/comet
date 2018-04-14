package com.vector.comet.util.http;

/**
 * Created by jinku on 2018/4/1.
 */
public interface HttpCallback {
    void onSuccess(String response);
    void onFailed(Exception e);
}
