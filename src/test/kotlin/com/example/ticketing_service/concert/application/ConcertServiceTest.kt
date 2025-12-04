package com.example.ticketing_service.concert.application

import com.example.ticketing_service.concert.domain.Concert
import com.example.ticketing_service.concert.infra.ConcertRepository
import com.example.ticketing_service.concert.infra.ConcertScheduleRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class ConcertServiceTest {
    @MockK
    lateinit var concertRepository : ConcertRepository

    @MockK
    lateinit var scheduleRepository: ConcertScheduleRepository

    @InjectMockKs
    lateinit var concertService : ConcertService

    @Test
    @DisplayName("공연 목록 조회시 DTO 리스트로 반환")
    fun get_concerts_success() {
        val concerts = listOf(
            Concert(title = "Concert A", description = "Desc A"),
            Concert(title = "Concert B", description = "Desc B")
        )

        // 리플렉션으로 id 주입
        concerts.forEachIndexed { index, concert ->
            val idField = Concert::class.java.getDeclaredField("id")
            idField.isAccessible = true
            idField.set(concert, (index + 1).toLong())
        }

        every { concertRepository.findAll() } returns concerts

        val result = concertService.getConcerts()

        assertEquals(2, result.size)
        assertEquals("Concert A", result[0].title)
    }
}