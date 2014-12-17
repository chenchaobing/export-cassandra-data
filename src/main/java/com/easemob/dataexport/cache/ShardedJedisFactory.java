package com.easemob.dataexport.cache;

import redis.clients.jedis.*;

import java.util.List;

public class ShardedJedisFactory {

    public static ShardedJedisPool createInstance(JedisPoolConfig config, List<JedisShardInfo> shards) {
        ShardedJedisPool pool = new ShardedJedisPool(config, shards);
        return pool;
    }
//    redis.url=127.0.0.1
//    redis.port=6379
//    redis.slave.url=127.0.0.1
//    redis.slave.port=7379
//    public static ShardedJedis create(){
//        List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
//        JedisPoolConfig config = new JedisPoolConfig();
//        JedisShardInfo si = new JedisShardInfo("localhost", 6379);
//        si.setPassword("foobared");
//        shards.add(si);
//        si = new JedisShardInfo("localhost", 6380);
//        shards.add(si);
//        ShardedJedisPool pool = new ShardedJedisPool(config, shards);
//        ShardedJedis jedis = pool.getResource();
//        jedis.set("a", "foo");
//        pool.returnResource(jedis);
//        ShardedJedis jedis2 = pool.getResource();
//        jedis.set("z", "bar");
//        pool.returnResource(jedis);
//        pool.destroy();
//    }
}
