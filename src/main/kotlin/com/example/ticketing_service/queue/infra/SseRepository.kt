package com.example.ticketing_service.queue.infra

import org.springframework.stereotype.Repository
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.ConcurrentHashMap

@Repository
class SseRepository {
    // 동시성 문제를 방지하기 위해 ConcurrentHashMap 사용
    private val emitters = ConcurrentHashMap<String, SseEmitter>()

    fun save(userId: String, emitter: SseEmitter) {
        emitters[userId] = emitter
    }

    fun delete(userId: String) {
        emitters.remove(userId)
    }

    fun get(userId: String): SseEmitter? {
        return emitters[userId]
    }
}