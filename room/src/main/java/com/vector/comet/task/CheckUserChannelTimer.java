package com.vector.comet.task;

import com.vector.comet.constants.RedisKey;
import com.vector.comet.context.UserChannelContext;
import com.vector.comet.util.redis.RedisUtil;
import com.vector.comet.util.thread.NamedThreadFactory;
import org.jboss.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by jinku on 2018/4/15.
 */
public class CheckUserChannelTimer {

    private static final int CHECK_PERIOD = 3 * 1000;

    private static Logger logger = LoggerFactory.getLogger(CheckUserChannelTimer.class);

    private ScheduledFuture<?> scheduledFuture;

    // 定时任务执行器
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1,
            new NamedThreadFactory("CheckUserChannelTimer", true));

    public void start() {
        scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                try {
                    checkUserChannel();
                } catch (Throwable t) { // 防御性容错
                    logger.error("Unexpected error occur at checkUserChannel, cause: " + t.getMessage(), t);
                }
            }
        }, CHECK_PERIOD, CHECK_PERIOD, TimeUnit.MILLISECONDS);
    }

    public void destroy() {
        try {
            // 取消重连定时器
            if (!scheduledFuture.isCancelled()) {
                scheduledFuture.cancel(true);
            }
        } catch (Throwable t) {
            logger.warn("Failed to cancel checkUserChannel timer", t);
        }
    }

    private void checkUserChannel() {
        Map<String, Channel> userChannelMap = UserChannelContext.getUserId2Channel();
        Iterator<String> userIterator = userChannelMap.keySet().iterator();
        while (userIterator.hasNext()) {
            String userId = userIterator.next();
            Channel userChannel = userChannelMap.get(userId);
            if (!userChannel.isOpen()) {
                userIterator.remove();
                RedisUtil.deleteKey(RedisKey.getUserId2IpKey(userId));
                continue;
            }
            Object object = UserChannelContext.getChannelsMap().get(userChannel);
            if (object == null) {
                userIterator.remove();
                RedisUtil.deleteKey(RedisKey.getUserId2IpKey(userId));
                continue;
            }
        }
    }
}
