package com.vector.comet.netty;

import com.alibaba.fastjson.JSONObject;
import com.vector.comet.entry.ResponseBean;
import com.vector.comet.util.netty.RequestUtil;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.http.*;
import org.jboss.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpHeaders.setContentLength;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by jinku on 2018/4/14.
 */
public abstract class AbstractHttpHandler extends SimpleChannelHandler {

    protected Map<Channel, Object> channelsMap = new ConcurrentHashMap<Channel, Object>();
    private Object object = new Object();

    protected Logger logger = null;

    public AbstractHttpHandler() {
        logger = LoggerFactory.getLogger(getClass());
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (!(e.getMessage() instanceof HttpRequest)) {
            super.messageReceived(ctx, e);
            return;
        }

        HttpRequest httpRequest = (HttpRequest) e.getMessage();
        String url = httpRequest.getUri();
        if (!url.startsWith(getUrl())) {
            sendHttpResponse(ctx, httpRequest, new DefaultHttpResponse(HTTP_1_1, NOT_FOUND));
            return;
        }

        String contentType = httpRequest.getHeader("Content-Type");
        if (!"application/x-www-form-urlencoded".equals(contentType)) {
            sendHttpResponse(ctx, httpRequest, new DefaultHttpResponse(HTTP_1_1, FORBIDDEN));
            return;
        }
        Map<String, String> paramsMap = RequestUtil.parseParam(httpRequest);
        System.out.println("request params" + paramsMap);
        logger.info("request url [{}] params [{}]", url, paramsMap);

        ResponseBean responseBean = doService(paramsMap);
        String dataJson = JSONObject.toJSONString(responseBean);
        logger.info("result json [{}]", dataJson);

        ChannelBuffer buf = ChannelBuffers.copiedBuffer(dataJson, CharsetUtil.UTF_8);
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK);
        response.setContent(buf);
        response.setHeader(HttpHeaders.Names.CONTENT_TYPE, "application/json; charset=UTF-8");
        response.setHeader(HttpHeaders.Names.CONTENT_LENGTH, String.valueOf(buf
                .readableBytes()));
        sendHttpResponse(ctx, httpRequest, response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        logger.error("CometNettyHandler exceptionCaught", e.getCause());
        super.exceptionCaught(ctx, e);
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        super.channelConnected(ctx, e);
        channelsMap.put(ctx.getChannel(), object);
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        super.channelDisconnected(ctx, e);
        channelsMap.remove(ctx.getChannel());
    }

    public void closeChannels() {
        Set<Channel> channelSet = channelsMap.keySet();
        for (Channel channel : channelSet) {
            try {
                channel.close();
            } catch (Throwable e) {
            }
        }
    }

    private static void sendHttpResponse(ChannelHandlerContext ctx, HttpRequest req, HttpResponse res) {
        if (res.getStatus().getCode() != 200) {
            res.setContent(ChannelBuffers.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8));
            setContentLength(res, res.getContent().readableBytes());
        }

        // Send the response and close the connection if necessary.
        ChannelFuture f = ctx.getChannel().write(res);
        if (!isKeepAlive(req) || res.getStatus().getCode() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    public abstract String getUrl();

    public abstract ResponseBean doService(Map<String, String> paramsMap);
}
