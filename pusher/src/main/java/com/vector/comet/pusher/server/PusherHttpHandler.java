package com.vector.comet.pusher.server;

import com.alibaba.fastjson.JSONObject;
import com.vector.comet.constants.BaseConstants;
import com.vector.comet.entry.ResponseBean;
import com.vector.comet.netty.AbstractHttpHandler;
import com.vector.comet.util.netty.RequestUtil;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.http.*;
import org.jboss.netty.util.CharsetUtil;

import java.util.List;
import java.util.Map;

import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpHeaders.setContentLength;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.*;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by jinku on 2018/4/14.
 */
@ChannelHandler.Sharable
public class PusherHttpHandler extends AbstractHttpHandler {

    @Override
    public String getUrl() {
        return BaseConstants.PUSHER_URL;
    }

    @Override
    public ResponseBean doService(Map<String, String> paramsMap) {
        return ResponseBean.getFromResponse("lijinku");
    }
}
