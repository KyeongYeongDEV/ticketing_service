package com.example.ticketing_service.payment.infra

import com.example.ticketing_service.global.exception.BusinessException
import com.example.ticketing_service.global.exception.ErrorCode
import com.example.ticketing_service.payment.domain.PaymentClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.math.BigDecimal
import java.util.Base64

@Component
class TossPaymentClient(
    @Value("\${payment.toss.secret-key}") private val secretKey: String,
    @Value("\${payment.toss.url}") private val confirmUrl: String
) : PaymentClient {

    private val restClient = RestClient.create()

    override fun confirm(paymentKey: String, orderId: String, amount: BigDecimal): String {
        val encodedKey = Base64.getEncoder().encodeToString("$secretKey:".toByteArray())

        val requestBody = mapOf(
            "paymentKey" to paymentKey,
            "orderId" to orderId,
            "amount" to amount
        )

        try {
            val response = restClient.post()
                .uri(confirmUrl)
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
}