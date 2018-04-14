package com.vector.comet.log;

import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Slf4JLoggerFactory;

/**
 * Created by jinku on 2018/4/1.
 */
public class NettyLogAdapter {

    public static void setNettyLoggerFactory() {
        InternalLoggerFactory factory = InternalLoggerFactory.getDefaultFactory();
        if (factory == null || !(factory instanceof Slf4JLoggerFactory)) {
            InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
        }
    }
}
