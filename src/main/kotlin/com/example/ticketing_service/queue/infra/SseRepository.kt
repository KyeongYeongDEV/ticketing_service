package com.example.ticketing_service.queue.infra

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.ConcurrentHashMap

@Repository
class SseRepository {
    // 동시성 문제를 방지하기 위해 ConcurrentHashMap 사용
    private val emitters = ConcurrentHashMap<String, SseEmitter>()
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun save(userId: String, emitter: SseEmitter): SseEmitter {
        // 타임아웃  Map에서 제거하는 콜백 등록
        emitter.onCompletion {
            logger.info("SSE 연결 종료 (Completed): $userId")
            this.delete(userId)
        }
        emitter.onTimeout {
            logger.warn("SSE 연결 타임아웃 (Timed Out): $userId")
            emitter.complete() // complete()를 호출하면 onCompletion이 이어서 실행됨
        }

        emitters[userId] = emitter
        logger.info("SseEmitter 저장 완료. 현재 접속자 수: ${emitters.size}, User: $userId")
        return emitter
    }

    fun get(userId: String): SseEmitter? {
        return emitters[userId]
    }

    fun delete(userId: String) {
        emitters.remove(userId)
        logger.debug("SseEmitter 삭제 완료: $userId")
    }
}