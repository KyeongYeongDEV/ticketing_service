package com.example.ticketing_service.queue.presentation

import com.example.ticketing_service.queue.application.WaitingQueueService
import com.example.ticketing_service.queue.infra.SseRepository
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@RestController
@RequestMapping("/api/queue")
class QueueController(
    private val waitingQueueService: WaitingQueueService,
    private val sseRepository: SseRepository
) {

    @PostMapping
    fun register(@RequestHeader("X-User-Id") userId: String): Long? {
        return waitingQueueService.registerQueue(userId)
    }

    // SSE 연결 엔드포인트
    // 클라이언트는 대기열 진입 후 이 API를 호출해서 연결을 유지합니다.
    @GetMapping("/connect", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun connectQueue(@RequestHeader("X-User-Id") userId: String): SseEmitter {
        // 연결 객체 생성 (타임아웃 10분 설정)
        val emitter = SseEmitter(60 * 1000L * 10)

        // 저장소에 등록
        sseRepository.save(userId, emitter)

        // 연결 종료/에러/타임아웃 시 삭제하는 콜백 등록
        emitter.onCompletion { sseRepository.delete(userId) }
        emitter.onTimeout { sseRepository.delete(userId) }
        emitter.onError { sseRepository.delete(userId) }

        // 최초 연결 시 더미 데이터 전송 (503 에러 방지용)
        try {
            emitter.send(SseEmitter.event().name("connect").data("Connected"))
        } catch (e: Exception) {
            sseRepository.delete(userId)
        }

        return emitter
    }

    @GetMapping("/rank")
    fun getRank(@RequestHeader("X-User-Id") userId: String): Long? {
        return waitingQueueService.getRank(userId)
    }
}