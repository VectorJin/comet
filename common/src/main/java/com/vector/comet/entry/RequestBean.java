package com.vector.comet.entry;

import com.vector.comet.constants.ParamName;

import java.util.Map;

/**
 * Created by jinku on 2018/3/31.
 */
public class RequestBean {

    private String url;
    private Map<String, String> params;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "RequestBean{" +
                "url='" + url + '\'' +
                ", params=" + params +
                '}';
    }

    public boolean checkRequest() {
        if (url == null || "".equals(url) || params == null || params.size() == 0) {
            return false;
        }
        String userId = getUserId();
        if (userId == null || "".equals(userId)) {
            return false;
        }
        return true;
    }

    public String getUserId() {
        return params.get(ParamName.USER_ID);
    }
}
