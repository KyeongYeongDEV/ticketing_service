package com.example.ticketing_service.queue.infra

import com.example.ticketing_service.global.common.RedisChannel
import org.slf4j.LoggerFactory
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.nio.charset.StandardCharsets

@Component
class RedisQueueListener(
    private val sseRepository: SseRepository
) : MessageListener {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun onMessage(message: Message, pattern: ByteArray?) {
        val userId = String(message.body, StandardCharsets.UTF_8)
        logger.debug("[Redis Sub] 브로드캐스트 메시지 수신 - User ID: $userId")

        val emitter = sseRepository.get(userId)

        if (emitter != null) {
            // 알림 전송
            logger.info("[SSE] 유저($userId)가 이 서버에 존재합니다. 입장 알림 전송.")
            try {
                emitter.send(SseEmitter.event().name("entry").data("입장 가능합니다."))
                // 연결 종료
                emitter.complete()
            } catch (e: Exception) {
                logger.error("SSE 전송 실패 - User: $userId", e)
                // 전송에 실패한 죽은 연결 삭제
                sseRepository.delete(userId)
            }
        } else {
            // 다른 서버와 연결된 유저
            logger.debug("[SSE] 유저($userId)는 이 서버에 없습니다. 무시합니다.")
        }
    }
}