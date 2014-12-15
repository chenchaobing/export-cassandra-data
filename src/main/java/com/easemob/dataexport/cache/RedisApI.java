package com.easemob.dataexport.cache;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisApI {
	
    private static JedisPool jedisPool = getPool();
    
    public static final int DEFAULT_EXPIRE_SECONDS = 7 * 24 * 60 * 60;
    
    public static JedisPool getPool() {
        if (jedisPool == null) {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(50);
            config.setMaxIdle(5);
            config.setMaxWaitMillis(1000);
            jedisPool = new JedisPool(config, "127.0.0.1", 6379);
        }  
        return jedisPool;  
    }
      
    public static void returnResource(JedisPool pool, Jedis redis) {
        if (redis != null) {
            pool.returnResource(redis);
        }
    }
      
    public static void set(String key , String value){  
        try (Jedis jedis = jedisPool.getResource()) {
        	jedis.set(key, value);
        	jedis.expire(key, DEFAULT_EXPIRE_SECONDS);
        }
    }
}
