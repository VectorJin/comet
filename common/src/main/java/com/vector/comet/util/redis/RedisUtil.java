package com.vector.comet.util.redis;

import com.vector.comet.util.config.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by jinku on 2018/4/15.
 */
public class RedisUtil {

    private static JedisPool pool;

    static {
        JedisPoolConfig config = new JedisPoolConfig();
        String redisHost = PropertiesUtil.getString("redis.host");
        int redisPort = PropertiesUtil.getInt("redis.port");
        pool = new JedisPool(config, redisHost, redisPort);
    }

    public static void setValue(String key, String value) {
        Jedis jedis = pool.getResource();
        try {
            jedis.set(key, value);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}
