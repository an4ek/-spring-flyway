package com.example.lab3.web.dto

data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String
)