package com.example.lab3.web.dto

data class CreateDishRequest(
    val name: String?,
    val description: String?,
    val price: Double?
)