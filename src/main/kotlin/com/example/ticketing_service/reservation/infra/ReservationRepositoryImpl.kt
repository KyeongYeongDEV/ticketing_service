package com.example.ticketing_service.reservation.infra

import com.example.ticketing_service.reservation.domain.Reservation
import com.example.ticketing_service.reservation.domain.ReservationRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
class ReservationRepositoryImpl (
    private val jpaRespository : ReservationJpaRepository
) : ReservationRepository {
    override fun save (reservation : Reservation) : Reservation {
        return jpaRespository.save(reservation)
    }

    override fun findById(id : Long) : Optional<Reservation> {
        return jpaRespository.findById(id)
    }

    /**
     * @Description Seat 정보를 JOIN FETCH하여 한 번에 가져오는 최적화된 조회 메서드입니다.
     * @Why 필요한가? 일반 findById는 Lazy Loading되어 N+1 쿼리 문제가 발생하므로,
     * 결제와 같이 트랜잭션이 중요한 곳에서는 Eager(즉시 로딩) 쿼리가 필요합니다.
     */
    override fun findByIdWithSeat(id: Long): Optional<Reservation> {
        // [수정] 일반 findById 대신 JPA 최적화 쿼리를 사용합니다.
        return jpaRepository.findByIdOptimized(id)
    }
}