package com.example.lab3.web.dto

data class DishCreateRequest(
    val name: String,
    val description: String,
    val price: Double,
    val isAvailable: Boolean
)

data class DishUpdateRequest(
    val name: String,
    val description: String,
    val price: Double,
    val isAvailable: Boolean
)

data class DishResponse(
    val id: Long,
    val name: String,
    val description: String,
    val price: Double,
    val isAvailable: Boolean
)