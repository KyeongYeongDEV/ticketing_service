package com.example.ticketing_service.global.config

import com.example.ticketing_service.global.common.RedisChannel
import com.example.ticketing_service.queue.infra.RedisQueueListener
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer

@Configuration
class RedissonConfig(
    @Value("\${spring.data.redis.host}") private val host: String,
    @Value("\${spring.data.redis.port}") private val port: Int
) {

    @Bean
    fun redissonClient(): RedissonClient {
        val config = Config()

        config.useSingleServer().address = "redis://$host:$port"
        return Redisson.create(config)
    }

    @Bean
    fun redisMessageListenerContainer(
        connectionFactory: RedisConnectionFactory,
        listener: RedisQueueListener
    ): RedisMessageListenerContainer {
        val container = RedisMessageListenerContainer()
        container.setConnectionFactory(connectionFactory)

        // queue:entry 채널을 구독하도록 리스너 연결
        container.addMessageListener(listener, ChannelTopic(RedisChannel.ENTRY))

        return container
    }
}