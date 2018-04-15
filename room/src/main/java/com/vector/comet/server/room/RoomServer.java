package com.vector.comet.server.room;

import com.vector.comet.netty.AbstractServer;
import com.vector.comet.task.CheckUserChannelTimer;
import org.jboss.netty.channel.*;

/**
 * Created by jinku on 2018/3/27.
 */
public class RoomServer extends AbstractServer {

    private RoomNettyHandler roomNettyHandler;
    private CheckUserChannelTimer checkUserChannelTimer;

    public RoomServer() {
        super();
        roomNettyHandler = new RoomNettyHandler();
        checkUserChannelTimer = new CheckUserChannelTimer();
        checkUserChannelTimer.start();
    }

    @Override
    public String getDesc() {
        return "RoomServer";
    }

    @Override
    public int getPort() {
        return 8080;
    }

    @Override
    public void addHandler(ChannelPipeline pipeline) {
        pipeline.addLast("web_socket_handler", new WebSocketServerHandler());
        pipeline.addLast("room_handler", roomNettyHandler);
    }

    @Override
    public void onClose() {
        roomNettyHandler.closeChannels();
        checkUserChannelTimer.destroy();
    }
}
