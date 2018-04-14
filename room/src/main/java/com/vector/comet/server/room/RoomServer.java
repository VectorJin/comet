package com.vector.comet.server.room;

import com.vector.comet.netty.AbstractServer;
import org.jboss.netty.channel.*;

/**
 * Created by jinku on 2018/3/27.
 */
public class RoomServer extends AbstractServer {

    private RoomNettyHandler roomNettyHandler;

    public RoomServer() {
        super();
        roomNettyHandler = new RoomNettyHandler();
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
    }
}
