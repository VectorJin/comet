package com.vector.comet.netty;

import com.vector.comet.log.NettyLogAdapter;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jinku on 2018/4/14.
 */
public abstract class AbstractServer implements Server {

    protected Logger logger = null;

    public final static int WORKER_COUNT = Runtime.getRuntime().availableProcessors() + 1;

    private volatile boolean isStarted;

    private ServerBootstrap bootstrap;
    private Channel channel;

    public AbstractServer() {
        logger = LoggerFactory.getLogger(getClass());
    }

    public void open() {
        if (isStarted) {
            return;
        }
        isStarted = true;
        NettyLogAdapter.setNettyLoggerFactory();
        ExecutorService boss = Executors.newCachedThreadPool(new NamedThreadFactory(getDesc() + "Boss", false));
        ExecutorService worker = Executors.newCachedThreadPool(new NamedThreadFactory(getDesc() + "Worker", false));
        ChannelFactory channelFactory = new NioServerSocketChannelFactory(boss, worker, WORKER_COUNT);
        bootstrap = new ServerBootstrap(channelFactory);

        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() {
                ChannelPipeline pipeline = Channels.pipeline();
                pipeline.addLast("decoder", new HttpRequestDecoder());
                pipeline.addLast("aggregator", new HttpChunkAggregator(65536));
                pipeline.addLast("encoder", new HttpResponseEncoder());
                addHandler(pipeline);
                return pipeline;
            }
        });
        SocketAddress bindAddress = new InetSocketAddress(getPort());
        channel = bootstrap.bind(bindAddress);
        logger.info(getDesc() + " server start !!!");
    }

    public void close() {
        if (channel != null) {
            // unbind.
            channel.close();
        }

        // channels关闭
        onClose();

        if (bootstrap != null) {
            // release external resource.
            bootstrap.releaseExternalResources();
        }
    }

    public abstract String getDesc();

    public abstract int getPort();

    public abstract void addHandler(ChannelPipeline pipeline);

    public abstract void onClose();

}
