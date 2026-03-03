package com.example.lab3.domain.model

data class Dish(
    val id: Long = 0,
    val name: String,
    val description: String,
    val price: Double,
    val isAvailable: Boolean,
    val restaurantId: Long? = null
)