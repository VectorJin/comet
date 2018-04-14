package com.vector.comet.util.netty;

import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.util.CharsetUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jinku on 2018/4/14.
 */
public class RequestUtil {

    public static Map<String, String> parseParam(HttpRequest httpRequest) {
        Map<String, String> paramsMap = new HashMap<String, String>();
        if (httpRequest == null) {
            return paramsMap;
        }
        String uri = httpRequest.getUri();
        String content = httpRequest.getContent().toString(CharsetUtil.UTF_8);
        if (uri.contains("?")) {
            content = uri.substring(uri.lastIndexOf("?") + 1) + "&" + content;
        }
        String[] paramPairArray = content.split("&");
        if (paramPairArray == null || paramPairArray.length == 0) {
            return paramsMap;
        }
        for (String paramPair : paramPairArray) {
            if (paramPair == null || "".equals(paramPair.replaceAll(" ", ""))) {
                continue;
            }
            if (!paramPair.contains("=")) {
                continue;
            }
            String[] keyValue = paramPair.split("=");
            if (keyValue == null || keyValue.length != 2) {
                continue;
            }
            if (keyValue[0] == null || "".equals(keyValue[0].replaceAll(" ", ""))) {
                continue;
            }
            if (keyValue[1] == null || "".equals(keyValue[1].replaceAll(" ", ""))) {
                continue;
            }
            paramsMap.put(keyValue[0], keyValue[1]);
        }
        return paramsMap;
    }
}
