package com.example.ticketing_service.concert.domain

import com.example.ticketing_service.common.entity.BaseEntity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "concert_schedules")
class ConcertSchedule (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", nullable = false)
    val concert : Concert,

    @Column(nullable = false)
    val concertDate : LocalDateTime,

    @Column(nullable = false)
    val totalSeats : Int

) : BaseEntity()