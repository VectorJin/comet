package com.vector.comet.pusher.server;

import com.alibaba.fastjson.JSONObject;
import com.vector.comet.constants.BaseConstants;
import com.vector.comet.constants.ErrorCode;
import com.vector.comet.constants.ParamName;
import com.vector.comet.constants.RedisKey;
import com.vector.comet.entry.ResponseBean;
import com.vector.comet.netty.AbstractHttpHandler;
import com.vector.comet.util.StringUtils;
import com.vector.comet.util.http.AsyncHttpClient;
import com.vector.comet.util.http.HttpCallback;
import com.vector.comet.util.netty.RequestUtil;
import com.vector.comet.util.redis.RedisUtil;
import org.apache.http.*;
import org.apache.http.HttpResponse;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.http.*;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.jboss.netty.util.CharsetUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

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
        // 业务系统推送消息
        // 检查用户是否在线
        String userId = paramsMap.get(ParamName.USER_ID);
        String ip = RedisUtil.getValue(RedisKey.getUserId2IpKey(userId));
        if (StringUtils.isEmpty(ip)) {
            return ResponseBean.getErrorResponse(ErrorCode.NOT_ONLINE);
        }

        // 调用对应room server
        String url = String.format("http://%s:%s%s", ip, BaseConstants.NOTICE_PORT, BaseConstants.NOTICE_URL);
        String response = AsyncHttpClient.getInstance().syncPost(url, paramsMap);

        if (StringUtils.isEmpty(response)) {
            return ResponseBean.getErrorResponse(ErrorCode.FAILED);
        }
        return ResponseBean.getFromResponse(response);
    }
}
