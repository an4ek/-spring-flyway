package com.example.lab3.cache

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import redis.embedded.RedisServer
import jakarta.annotation.PreDestroy

@TestConfiguration
class EmbeddedRedisConfig {

    @Value("\${spring.data.redis.port:6370}")
    private var redisPort: Int = 6370

    private var redisServer: RedisServer? = null

    @Bean(initMethod = "start")
    fun redisServer(): RedisServer {
        redisServer = RedisServer(redisPort)
        return redisServer!!
    }

    @PreDestroy
    fun stopRedis() {
        redisServer?.stop()
    }
}
