package com.example.ticketing_service.queue.infra

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.ConcurrentHashMap

@Repository
class SseRepository {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val emitters = ConcurrentHashMap<String, SseEmitter>()

    fun save(userId: String, emitter: SseEmitter): SseEmitter {
        // 새 연결 들어올 시 기존 연결은 만료
        emitters[userId]?.let { oldEmitter ->
            logger.warn("중복 로그인 감지 - 기존 연결 종료: $userId")
            oldEmitter.complete()
        }

        // 콜백 등록
        emitter.onCompletion {
            logger.info("SSE 연결 종료 (Completed): $userId")
            // 맵에 저장된 객체가 '현재 이 emitter'일 때만 삭제
            emitters.remove(userId, emitter)
        }

        emitter.onTimeout {
            logger.warn("SSE 연결 타임아웃 (Timed Out): $userId")
            emitter.complete()
        }

        //  onError 추가 - 네트워크 에러 등으로 끊겼을 때
        emitter.onError { e ->
            logger.error("SSE 연결 에러 (Error): $userId", e)
            emitter.complete()
        }

        // 저장
        emitters[userId] = emitter
        logger.info("SseEmitter 저장 완료. User: $userId")
        return emitter
    }

    fun get(userId: String): SseEmitter? {
        return emitters[userId]
    }

    fun delete(userId: String) {
        emitters.remove(userId)
    }
}