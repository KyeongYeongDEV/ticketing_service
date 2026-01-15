package com.example.ticketing_service.global.config

import com.example.ticketing_service.global.interceptor.WaitingQueueInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val waitingQueueInterceptor: WaitingQueueInterceptor
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(waitingQueueInterceptor)
            // 검사가 필요한 로직 (예약, 결제)
            .addPathPatterns("/api/reservations/**", "/api/payments/**")
            // 검사하면 안 되는 로직 (대기열 등록/조회, 회원가입 등)
            .excludePathPatterns("/api/queue/**", "/api/auth/**", "/h2-console/**")
    }
}