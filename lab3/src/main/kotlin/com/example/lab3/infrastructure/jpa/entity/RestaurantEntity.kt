package com.example.lab3.infrastructure.jpa.entity

import jakarta.persistence.*

@Entity
@Table(name = "restaurants")
class RestaurantEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var address: String,

    @OneToMany(mappedBy = "restaurant", fetch = FetchType.LAZY)
    var dishes: MutableList<DishEntity> = mutableListOf()
)