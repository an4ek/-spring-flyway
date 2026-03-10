package com.example.lab3.web.dto

data class CreateUserRequest(
    val email: String?,
    val firstName: String?,
    val lastName: String?,
    val isActive: Boolean = true
)