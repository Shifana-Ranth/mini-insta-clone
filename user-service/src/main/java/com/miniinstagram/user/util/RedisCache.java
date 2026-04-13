package com.miniinstagram.user.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisCache {
    // JedisPool is thread-safe and perfect for Servlets
    private static final JedisPool jedisPool;

    static {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128);
        poolConfig.setMaxIdle(64);
        poolConfig.setMinIdle(16);
        
        // Connect to localhost on default port 6379
        jedisPool = new JedisPool(poolConfig, "localhost", 6379);
    }

    // Get a connection from the pool
    public static Jedis getJedis() {
        return jedisPool.getResource();
    }

    // Call this when the server shuts down
    public static void closePool() {
        if (jedisPool != null) {
            jedisPool.close();
        }
    }
}