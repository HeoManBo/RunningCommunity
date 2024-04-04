package com.example.runningweb.config.redis;


import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisServer;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;


/**
 * Redis 관련 설정
 */

@Configuration
@EnableRedisRepositories
public class EmbeddedRedisConfig {

    @Value("${spring.redis.port}")
    private int port;
    @Value("spring.redis.host")
    private String host;

    private RedisServer redisServer; // Docker redis : 반드시 docker redis가 켜져 있어야 함


    @PostConstruct
    public void init(){
        redisServer = new RedisServer(host, port);
    }

//    @PreDestroy
//    public void end(){
//        if (redisServer != null) {
//            redisServer
//        }
//    }
}
