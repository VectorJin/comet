package com.vector.comet.server.room;

import com.alibaba.fastjson.JSONObject;
import com.vector.comet.constants.ErrorCode;
import com.vector.comet.constants.RedisKey;
import com.vector.comet.context.UserChannelContext;
import com.vector.comet.entry.RequestBean;
import com.vector.comet.entry.ResponseBean;
import com.vector.comet.util.IpUtils;
import com.vector.comet.util.http.AsyncHttpClient;
import com.vector.comet.util.http.HttpCallback;
import com.vector.comet.util.redis.RedisUtil;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jinku on 2018/3/27.
 */
@ChannelHandler.Sharable
public class RoomNettyHandler extends SimpleChannelHandler {

    private static Logger logger = LoggerFactory.getLogger(RoomNettyHandler.class);

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (!(e.getMessage() instanceof TextWebSocketFrame)) {
            super.messageReceived(ctx, e);
            return;
        }

        // dispatch request
        // check request
        String requestStr = ((TextWebSocketFrame) e.getMessage()).getText();
        logger.error("CometNettyHandler messageReceived requestStr[{}]", requestStr);
        RequestBean requestBean = null;
        try {
            requestBean = JSONObject.parseObject(requestStr, RequestBean.class);
        } catch (Exception exception) {
            logger.error("CometNettyHandler messageReceived request illegal");
            ResponseBean responseBean = ResponseBean.getErrorResponse(ErrorCode.BAD_REQUEST);
            ctx.getChannel().write(new TextWebSocketFrame(JSONObject.toJSONString(responseBean)));
            return;
        }

        if (!requestBean.checkRequest()) {
            ResponseBean responseBean = ResponseBean.getErrorResponse(ErrorCode.BAD_REQUEST);
            ctx.getChannel().write(new TextWebSocketFrame(JSONObject.toJSONString(responseBean)));
            return;
        }
        String userId = requestBean.getUserId();
        final Channel channel = ctx.getChannel();
        UserChannelContext.mapUserChannel(userId, channel);
        // 对应关系写入redis中
        // 获取本地IP
        String localIp = IpUtils.getIp();
        String userIdKey = RedisKey.getUserId2IpKey(userId);
        RedisUtil.setValue(userIdKey, localIp);

        // 异步请求
        AsyncHttpClient.getInstance().asyncPost(requestBean.getUrl(), requestBean.getParams(), new HttpCallback() {
            public void onSuccess(String response) {
                ResponseBean responseBean = ResponseBean.getFromResponse(response);
                channel.write(new TextWebSocketFrame(JSONObject.toJSONString(responseBean)));
            }
            public void onFailed(Exception e) {
                ResponseBean responseBean = ResponseBean.getErrorResponse(ErrorCode.FAILED);
                if (e != null) {
                    responseBean.setErrorMsg(e.getMessage());
                }
                channel.write(new TextWebSocketFrame(JSONObject.toJSONString(responseBean)));
            }
        });
    }

    @Override
    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        super.writeRequested(ctx, e);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        logger.error("CometNettyHandler exceptionCaught", e.getCause());
        super.exceptionCaught(ctx, e);
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        super.channelConnected(ctx, e);
        UserChannelContext.addChannel(ctx.getChannel());
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        super.channelDisconnected(ctx, e);
        UserChannelContext.removeChannel(ctx.getChannel());
    }

    public void closeChannels() {
        UserChannelContext.closeChannels();
    }
}
