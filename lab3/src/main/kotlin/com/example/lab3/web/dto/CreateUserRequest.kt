package com.example.lab3.web.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class CreateUserRequest(
     @field:Email(message = "Email некорректен")
    @field:NotBlank(message = "Email не может быть пустым")
    val email: String,

    @field:NotBlank(message = "Имя не может быть пустым")
    val firstName: String,

    @field:NotBlank(message = "Фамилия не может быть пустой")
    val lastName: String,

    val isActive: Boolean? = true
)