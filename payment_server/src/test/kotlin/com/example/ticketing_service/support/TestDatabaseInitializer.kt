package com.example.ticketing_service.support

import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TestDatabaseInitializer {

    @Autowired
    private lateinit var entityManager: EntityManager

    @Transactional
    fun clearAndInitialize() {
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate()

        val tables = listOf("payments", "reservations", "seats", "concert_schedules", "concerts", "users")

        for (table in tables) {
            entityManager.createNativeQuery("TRUNCATE TABLE $table").executeUpdate()
        }

        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate()

        // ID를 1로 고정하기 위해 Native Query 사용
        entityManager.createNativeQuery(
            """
            INSERT INTO users (id, name, email, password, created_at, updated_at) 
            VALUES (1, 'Tester', 'test@test.com', '1234', NOW(), NOW())
            """.trimIndent()
        ).executeUpdate()

        entityManager.createNativeQuery(
            """
            INSERT INTO concerts (id, title, description, created_at, updated_at) 
            VALUES (1, 'Test Concert', 'Desc', NOW(), NOW())
            """.trimIndent()
        ).executeUpdate()

        entityManager.createNativeQuery(
            """
            INSERT INTO concert_schedules (id, concert_id, concert_date, total_seats, created_at, updated_at) 
            VALUES (1, 1, NOW(), 100, NOW(), NOW())
            """.trimIndent()
        ).executeUpdate()

        entityManager.createNativeQuery(
            """
            INSERT INTO seats (id, schedule_id, seat_no, price, status, version, created_at, updated_at) 
            VALUES (1, 1, 1, 10000, 'AVAILABLE', 0, NOW(), NOW())
            """.trimIndent()
        ).executeUpdate()
    }
}