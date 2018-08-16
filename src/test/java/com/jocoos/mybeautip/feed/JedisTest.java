package com.jocoos.mybeautip.feed;

import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Tuple;

public class JedisTest {

  public static void main(String[] args) throws InterruptedException {
    JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
    JedisPool jedisPool = new JedisPool(jedisPoolConfig, "localhost", 6379, 2000, "akdlqbxlq#1@Jocoos");
    Jedis jedis = jedisPool.getResource();
    System.out.println(jedis);

    Set<Tuple> tuples = jedis.zrevrangeByScoreWithScores("mycom", "+inf", "1998");
    for(Tuple t: tuples) {
      System.out.println(t.getScore() + ", " + t.getElement());
    }

    jedis.disconnect();
    jedis.close();
  }
}
