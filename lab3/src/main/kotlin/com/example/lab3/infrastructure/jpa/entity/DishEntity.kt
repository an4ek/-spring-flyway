package com.example.lab3.infrastructure.jpa.entity

import jakarta.persistence.*

@Entity
@Table(name = "dishes")
class DishEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var description: String,

    @Column(nullable = false)
    var price: Double,

    @Column(nullable = false)
    var isAvailable: Boolean,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    var restaurant: RestaurantEntity? = null,

    @ManyToMany(mappedBy = "dishes", fetch = FetchType.LAZY)
    var orders: MutableList<OrderEntity> = mutableListOf()
)