package com.example.ticketing_service.concert.domain

import com.example.ticketing_service.common.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table


@Entity
@Table(name = "concerts")
class Concert (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id : Long? = null,

    @Column(nullable = false)
    val title : String,

    @Column(nullable = false)
    val description : String
) : BaseEntity()