package com.vector.comet.constants;

/**
 * Created by jinku on 2018/4/15.
 */
public class RedisKey {

    public final static String REDIS_PREFIX = "comet:";

    public static String getUserId2IpKey(String userId) {
        String key = REDIS_PREFIX + "UserId2Ip:" + userId;
        return key;
    }
}
