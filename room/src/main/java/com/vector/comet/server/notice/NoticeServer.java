package com.vector.comet.server.notice;

import com.vector.comet.netty.AbstractServer;
import org.jboss.netty.channel.ChannelPipeline;

/**
 * Created by jinku on 2018/4/14.
 */
public class NoticeServer extends AbstractServer {

    private NoticeHttpHandler noticeNettyHandler;

    public NoticeServer() {
        super();
        noticeNettyHandler = new NoticeHttpHandler();
    }

    @Override
    public String getDesc() {
        return "NoticeServer";
    }

    @Override
    public int getPort() {
        return 8081;
    }

    @Override
    public void addHandler(ChannelPipeline pipeline) {
        pipeline.addLast("notice_handler", noticeNettyHandler);
    }

    @Override
    public void onClose() {
        noticeNettyHandler.closeChannels();
    }
}
