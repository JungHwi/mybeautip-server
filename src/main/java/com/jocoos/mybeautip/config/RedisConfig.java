package com.jocoos.mybeautip.config;

//@Data
//@Configuration
//@ConfigurationProperties("redis")
public class RedisConfig {

//    private String host;
//    private int port;
//    private String password;
//
//    @Bean
//    public JedisPool jedisPool() {
//        JedisPoolConfig poolConfig = new JedisPoolConfig();
//        poolConfig.setMaxTotal(128);
//        poolConfig.setMaxIdle(128);
//        poolConfig.setMinIdle(16);
//        poolConfig.setTestOnBorrow(true);
//        poolConfig.setTestOnReturn(true);
//        poolConfig.setTestWhileIdle(true);
//        poolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(60).toMillis());
//        poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(30).toMillis());
//        poolConfig.setNumTestsPerEvictionRun(3);
//        poolConfig.setBlockWhenExhausted(true);
//
//        return new JedisPool(poolConfig, host, port, 3600, password);
//    }
}
