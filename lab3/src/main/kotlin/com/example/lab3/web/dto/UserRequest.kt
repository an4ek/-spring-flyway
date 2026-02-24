package com.example.lab3.dto

import jakarta.validation.constraints.NotBlank

data class UserRequest(
    @field:NotBlank(message = "email не должен быть пустым")
    val email: String,

    @field:NotBlank(message = "firstName не должен быть пустым")
    val firstName: String,

    @field:NotBlank(message = "lastName не должен быть пустым")
    val lastName: String
)