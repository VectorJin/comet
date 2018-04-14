package com.vector.comet.base;

import com.vector.comet.util.http.AsyncHttpClient;

/**
 * Created by jinku on 2018/4/1.
 */
public class ResourceManage {

    public static void clearResource() {
        AsyncHttpClient.getInstance().close();
    }
}
