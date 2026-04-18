package com.example.lab3.web.dto

import jakarta.validation.constraints.NotBlank

data class RestaurantCreateRequest(
    @field:NotBlank(message = "Название не может быть пустым")
    val name: String,

    @field:NotBlank(message = "Адрес не может быть пустым")
    val address: String
)

data class RestaurantUpdateRequest(
    @field:NotBlank(message = "Название не может быть пустым")
    val name: String,

    @field:NotBlank(message = "Адрес не может быть пустым")
    val address: String
)