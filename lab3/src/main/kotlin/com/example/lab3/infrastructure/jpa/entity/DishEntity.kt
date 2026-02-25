package com.example.lab3.infrastructure.jpa.entity

import jakarta.persistence.*

@Entity
@Table(name = "dishes")
data class DishEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var description: String,

    @Column(nullable = false)
    var price: Double,

    @Column(nullable = false)
    var isAvailable: Boolean
)