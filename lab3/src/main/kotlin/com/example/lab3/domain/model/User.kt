package com.example.lab3.domain.model

data class User(
    val id: Long = 0,
    val email: String,
    val firstName: String,
    val lastName: String,
    val isActive: Boolean = true
)