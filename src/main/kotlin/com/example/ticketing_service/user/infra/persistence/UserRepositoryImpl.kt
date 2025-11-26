package com.example.ticketing_service.user.infra.persistence

import com.example.ticketing_service.user.domain.User
import com.example.ticketing_service.user.domain.UserRepository
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    private val jpaRepository : UserJpaRepository
) : UserRepository {
    override fun save(user: User): User {
        return jpaRepository.save(user);
    }

    override fun findById(id: Long): User? {
        return jpaRepository.findById(id).orElse(null)
    }
}