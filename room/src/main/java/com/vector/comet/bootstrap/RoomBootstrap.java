package com.vector.comet.bootstrap;

import com.vector.comet.base.ResourceManage;
import com.vector.comet.netty.Server;
import com.vector.comet.server.notice.NoticeServer;
import com.vector.comet.server.room.RoomServer;

/**
 * Created by jinku on 2018/4/1.
 */
public class RoomBootstrap {

    public static void main(String[] args) {
        final Server roomServer = new RoomServer();
        final Server noticeServer = new NoticeServer();

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                roomServer.close();
                noticeServer.close();
                ResourceManage.clearResource();
            }
        }, "RoomShutdownHook"));

        roomServer.open();
        noticeServer.open();
    }
}
