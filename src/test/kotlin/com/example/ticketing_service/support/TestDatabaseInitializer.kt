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
        // 1. 제약 조건 비활성화 및 테이블 초기화
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate()
        val tables = listOf("reservations", "seats", "concert_schedules", "concerts", "users")

        for (table in tables) {
            entityManager.createNativeQuery("TRUNCATE TABLE $table").executeUpdate()
        }

        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate()

        // 2. 테스트 데이터 삽입 (ID를 1로 고정하기 위해 Native Query 사용)
        // (1) 유저 생성
        entityManager.createNativeQuery(
            "INSERT INTO users (id, name, email, point, created_at, updated_at) VALUES (1, 'Tester', 'test@test.com', 0, NOW(), NOW())"
        ).executeUpdate()

        // (2) 콘서트 및 스케줄 생성 (Seat의 부모 데이터)
        entityManager.createNativeQuery(
            "INSERT INTO concert (id, title, description, created_at, updated_at) VALUES (1, 'Test Concert', 'Desc', NOW(), NOW())"
        ).executeUpdate()

        entityManager.createNativeQuery(
            "INSERT INTO concert_schedule (id, concert_id, concert_date, total_seats, created_at, updated_at) VALUES (1, 1, NOW(), 100, NOW(), NOW())"
        ).executeUpdate()

        // (3) 좌석 생성 (ID=1, AVAILABLE 상태)
        // version 컬럼은 낙관적 락 테스트를 위해 0으로 초기화
        entityManager.createNativeQuery(
            "INSERT INTO seat (id, schedule_id, seat_no, price, status, version, created_at, updated_at) VALUES (1, 1, 1, 10000, 'AVAILABLE', 0, NOW(), NOW())"
        ).executeUpdate()
    }
}