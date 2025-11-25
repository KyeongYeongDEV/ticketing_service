package com.example.ticketing_service.user.infra.persistence

import com.example.ticketing_service.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserJpaRepository : JpaRepository<User, Long>