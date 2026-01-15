package com.example.ticketing_service.payment.infra

import com.example.ticketing_service.global.exception.BusinessException
import com.example.ticketing_service.global.exception.ErrorCode
import com.example.ticketing_service.payment.domain.PaymentClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.math.BigDecimal
import java.util.Base64

@Component
class TossPaymentClient(
    @Value("\${payment.toss.secret-key}") private val secretKey: String,
    @Value("\${payment.toss.url}") private val confirmUrl: String
) : PaymentClient {

    private val restClient = RestClient.builder()
        .baseUrl(confirmUrl)
        .requestFactory(
            SimpleClientHttpRequestFactory().apply {
                setReadTimeout(10000)   // 읽기 타임아웃 10초
                setConnectTimeout(5000) // 연결 타임아웃 5초
            }
        )
        .build()

    override fun confirm(paymentKey: String, orderId: String, amount: BigDecimal): String {
        val encodedKey = Base64.getEncoder().encodeToString("$secretKey:".toByteArray())

        val requestBody = mapOf(
            "paymentKey" to paymentKey,
            "orderId" to orderId,
            "amount" to amount
        )

        try {
            val response = restClient.post()
                .uri("")
                .header("Authorization", "Basic $encodedKey")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .toEntity(String::class.java)

            if (!response.statusCode.is2xxSuccessful) {
                throw BusinessException(ErrorCode.PAYMENT_FAILED)
            }
            return paymentKey
        } catch (e: Exception) {
            throw BusinessException(ErrorCode.PAYMENT_FAILED)
        }
    }

    override fun validatePayment(orderId: String): String {
        try {
            val encodedKey = Base64.getEncoder().encodeToString("$secretKey:".toByteArray())

            // 조회 API용 URL로 교체 필요
            val checkUrl = "https://api.tosspayments.com/v1/payments/orders/$orderId"

            val response = RestClient.create().get() // 새 클라이언트 사용 (URL 충돌 방지)
                .uri(checkUrl)
                .header("Authorization", "Basic $encodedKey")
                .retrieve()
                .toEntity(String::class.java)

            // 200 OK면 결제된 것
            return if (response.statusCode.is2xxSuccessful) "DONE" else "UNKNOWN"
        } catch (e: Exception) {
            // 404 등 에러면 결제 안 된 것
            return "NOT_FOUND"
        }
    }
}