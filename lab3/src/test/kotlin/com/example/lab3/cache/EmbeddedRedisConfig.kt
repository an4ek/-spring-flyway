package com.example.lab3.cache

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import redis.embedded.RedisServer

@TestConfiguration
class EmbeddedRedisConfig {

    @Value("\${spring.data.redis.port:6379}")
    private var redisPort: Int = 6379

    @Value("\${use.embedded.redis:true}")
    private var useEmbeddedRedis: Boolean = true

    private var redisServer: RedisServer? = null

    @Bean
    fun redisServer(): String {
        if (useEmbeddedRedis) {
            redisServer = RedisServer(redisPort)
            redisServer!!.start()
        }
        return "redis-config"
    }

    @jakarta.annotation.PreDestroy
    fun stopRedis() {
        if (useEmbeddedRedis) {
            redisServer?.stop()
        }
    }
}
