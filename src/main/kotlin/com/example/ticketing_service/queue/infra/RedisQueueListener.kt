package com.example.ticketing_service.queue.infra

import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@Component
class RedisQueueListener(
    private val sseRepository: SseRepository
) : MessageListener {

    override fun onMessage(message: Message, pattern: ByteArray?) {
        // Redis 채널에서 메시지(userId)가 옴
        val userId = String(message.body)

        // 내 서버에 연결된 유저인지 확인
        val emitter = sseRepository.get(userId)

        if (emitter != null) {
            try {
                // 사용자에게 알림 전송
                emitter.send(
                    SseEmitter.event()
                        .name("entry") // 이벤트 이름
                        .data("입장 가능") // 데이터
                )
                // 알림 보냈으니 연결 종료
                emitter.complete()
            } catch (e: Exception) {
                sseRepository.delete(userId)
            }
        }
    }
}