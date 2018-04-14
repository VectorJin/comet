package com.vector.comet.pusher.boostrap;

import com.vector.comet.base.ResourceManage;
import com.vector.comet.pusher.server.PushServer;

/**
 * Created by jinku on 2018/4/14.
 */
public class PusherBootstrap {
    public static void main(String[] args) {
        final PushServer server = new PushServer();

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                server.close();
                ResourceManage.clearResource();
            }
        }, "PusherShutdownHook"));

        server.open();
    }
}
