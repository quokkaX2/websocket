package com.example.rediswebsocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RedisWebSocketApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisWebSocketApplication.class, args);
    }

}

