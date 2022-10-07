package com.jocoos.mybeautip.config;

//@Data
//@Configuration
//@ConfigurationProperties("redis")
public class RedisConfig {

//    private String host;
//    private int port;
//    private String password;
//
//    private int maxIdle = 128;
//    private int minIdle = 16;
//
//    @Bean
//    public JedisPool jedisPool() {
//        JedisPoolConfig poolConfig = new JedisPoolConfig();
//        poolConfig.setMaxTotal(maxIdle);
//        poolConfig.setMaxIdle(maxIdle);
//        poolConfig.setMinIdle(minIdle);
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
//
//    @Bean
//    public JedisCluster jedisCluster() {
//        Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
//        jedisClusterNodes.add(new HostAndPort(host, port));
//
//        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
//        jedisPoolConfig.setMaxIdle(maxIdle);
//        jedisPoolConfig.setMinIdle(minIdle);
//
//        JedisCluster jedisCluster = new JedisCluster(jedisClusterNodes);
//        return jedisCluster;
//    }
}
