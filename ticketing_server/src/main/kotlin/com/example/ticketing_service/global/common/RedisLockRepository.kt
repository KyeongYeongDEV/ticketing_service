package com.example.ticketing_service.global.common

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class RedisLockRepository (
    private val redisTemplate : StringRedisTemplate

){
    // Lua Script : 키의 값이 내 토큰과 같을 때만 삭제
    private val UNLOCK_SCRIPT = """
        if redis.call('get', KEYS[1] ) == ARGV[1] then
            return redis.call('del', KEYS[1])
        else 
            return 0
        end
    """.trimIndent()

    // 락 획득
    fun lock(key : String, token : String, ttl : Long) : Boolean {
        return redisTemplate.opsForValue()
            .setIfAbsent(key, token, Duration.ofMillis(ttl)) == true
    }


    // 락 해제 / 루아 스트립트 실행
    fun unlock(key : String, token : String) : Boolean {
        val script = DefaultRedisScript(UNLOCK_SCRIPT, Long::class.java)
        val result = redisTemplate.execute(script, listOf(key), token)

        return result == 1L
    }
}
