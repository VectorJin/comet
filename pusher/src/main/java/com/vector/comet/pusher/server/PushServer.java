package com.vector.comet.pusher.server;

import com.vector.comet.log.NettyLogAdapter;
import com.vector.comet.netty.AbstractServer;
import com.vector.comet.util.thread.NamedThreadFactory;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jinku on 2018/4/14.
 */
public class PushServer extends AbstractServer {

    private PusherHttpHandler httpHandler;

    public PushServer() {
        super();
        httpHandler = new PusherHttpHandler();
    }

    @Override
    public String getDesc() {
        return "PushServer";
    }

    @Override
    public int getPort() {
        return 8081;
    }

    @Override
    public void addHandler(ChannelPipeline pipeline) {
        pipeline.addLast("pusher_handler", httpHandler);
    }

    @Override
    public void onClose() {
        httpHandler.closeChannels();
    }
}
