package com.example.lab3.infrastructure.cache

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

@Configuration
class CacheConfig {

    @Bean
    fun cacheManager(connectionFactory: RedisConnectionFactory): RedisCacheManager {
        val ptv = BasicPolymorphicTypeValidator.builder()
            .allowIfBaseType(Any::class.java)
            .build()

        val objectMapper = ObjectMapper().apply {
            findAndRegisterModules()
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.EVERYTHING)
        }

        val serializer = GenericJackson2JsonRedisSerializer(objectMapper)
        val serializationPair = RedisSerializationContext.SerializationPair
            .fromSerializer(serializer)

        fun config(ttl: Duration) = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(ttl)
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer())
            )
            .serializeValuesWith(serializationPair)
            .disableCachingNullValues()

        return RedisCacheManager.builder(connectionFactory)
            .withCacheConfiguration("restaurants", config(Duration.ofHours(1)))
            .withCacheConfiguration("dishes", config(Duration.ofHours(1)))
            .cacheDefaults(config(Duration.ofMinutes(5)))
            .build()
    }
}
