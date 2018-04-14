package com.vector.comet.server.notice;

import com.alibaba.fastjson.JSONObject;
import com.vector.comet.constants.BaseConstants;
import com.vector.comet.constants.ErrorCode;
import com.vector.comet.constants.ParamName;
import com.vector.comet.context.UserChannelContext;
import com.vector.comet.entry.ResponseBean;
import com.vector.comet.netty.AbstractHttpHandler;
import com.vector.comet.util.netty.RequestUtil;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.http.*;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.jboss.netty.util.CharsetUtil;

import java.util.Map;

import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpHeaders.setContentLength;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by jinku on 2018/4/14.
 */
@ChannelHandler.Sharable
public class NoticeHttpHandler extends AbstractHttpHandler {

    @Override
    public String getUrl() {
        return BaseConstants.NOTICE_URL;
    }

    @Override
    public ResponseBean doService(Map<String, String> paramsMap) {
        String userId = paramsMap.get(ParamName.USER_ID);
        String busContent = paramsMap.get(ParamName.BUS_CONTENT);
        if (userId == null || "".equals(userId)) {
            return ResponseBean.getErrorResponse(ErrorCode.BAD_REQUEST);
        }
        if (busContent == null || "".equals(busContent)) {
            return ResponseBean.getErrorResponse(ErrorCode.BAD_REQUEST);
        }

        // 获取对应的Channel
        Channel channel = UserChannelContext.getUserChannel(userId);
        // 将消息写给用户端
        channel.write(null);
        ResponseBean responseBean = ResponseBean.getFromResponse(busContent);
        channel.write(new TextWebSocketFrame(JSONObject.toJSONString(responseBean)));

        // 返回给pusher
        return ResponseBean.getFromResponse(null);
    }
}
