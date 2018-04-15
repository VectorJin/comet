package com.vector.comet.context;

import org.jboss.netty.channel.Channel;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jinku on 2018/4/14.
 */
public class UserChannelContext {

    // 保存用户 --> Channel对应关系
    private static Map<String, Channel> userId2Channel = new ConcurrentHashMap<String, Channel>();
    private static Map<Channel, Object> channelsMap = new ConcurrentHashMap<Channel, Object>();
    private static Object object = new Object();

    public static void addChannel(Channel channel) {
        channelsMap.put(channel, object);
    }

    public static void removeChannel(Channel channel) {
        channelsMap.remove(channel);
    }

    public static void mapUserChannel(String userId, Channel channel) {
        userId2Channel.put(userId, channel);
    }

    public static Channel getUserChannel(String userId) {
        Channel channel = userId2Channel.get(userId);
        if (channel == null) {
            return null;
        }
        if (!channelsMap.containsKey(channel)) {
            return null;
        }
        if (!channel.isOpen()) {
            return null;
        }
        return channel;
    }

    public static void closeChannels() {
        userId2Channel.clear();
        Set<Channel> channelSet = channelsMap.keySet();
        for (Channel channel : channelSet) {
            try {
                channel.close();
            } catch (Throwable e) {
            }
        }
    }

    public static Map<Channel, Object> getChannelsMap() {
        return channelsMap;
    }

    public static Map<String, Channel> getUserId2Channel() {
        return userId2Channel;
    }
}
