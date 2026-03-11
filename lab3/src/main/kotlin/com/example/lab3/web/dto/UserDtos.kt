package com.example.lab3.web.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class UserCreateRequest(
    @field:Email(message = "Некорректный email")
    @field:NotBlank(message = "Email обязателен")
    val email: String?,

    @field:NotBlank(message = "Имя обязательно")
    val firstName: String?,

    @field:NotBlank(message = "Фамилия обязательна")
    val lastName: String?,

    val isActive: Boolean? = true
)

data class UserUpdateRequest(
    @field:Email(message = "Некорректный email")
    @field:NotBlank(message = "Email обязателен")
    val email: String?,

    @field:NotBlank(message = "Имя обязательно")
    val firstName: String?,

    @field:NotBlank(message = "Фамилия обязательна")
    val lastName: String?,

    val isActive: Boolean? = true
)