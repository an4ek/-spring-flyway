package com.example.lab3.web.dto

data class UserCreateRequest(
    val email: String,
    val firstName: String,
    val lastName: String,
    val isActive: Boolean? = true
)

data class UserUpdateRequest(
    val email: String,
    val firstName: String,
    val lastName: String,
    val isActive: Boolean
)

data class UserResponse(
    val id: Long,
    val email: String,
    val firstName: String,
    val lastName: String,
    val isActive: Boolean
)