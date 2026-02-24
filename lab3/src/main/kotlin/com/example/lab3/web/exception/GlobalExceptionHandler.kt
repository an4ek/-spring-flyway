package com.example.lab3.web.exception

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(
        ex: NotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<Map<String, Any?>> {
        val body = mapOf(
            "timestamp" to LocalDateTime.now(),
            "status" to 404,
            "error" to "Not Found",
            "message" to (ex.message ?: "Resource not found"),
            "path" to request.requestURI
        )
        return ResponseEntity.status(404).body(body)
    }

    @ExceptionHandler(ValidationException::class)
    fun handleValidation(
        ex: ValidationException,
        request: HttpServletRequest
    ): ResponseEntity<Map<String, Any?>> {
        val body = mapOf(
            "timestamp" to LocalDateTime.now(),
            "status" to 400,
            "error" to "Bad Request",
            "message" to (ex.message ?: "Validation error"),
            "path" to request.requestURI
        )
        return ResponseEntity.badRequest().body(body)
    }

    @ExceptionHandler(Exception::class)
    fun handleOther(
        ex: Exception,
        request: HttpServletRequest
    ): ResponseEntity<Map<String, Any?>> {
        val body = mapOf(
            "timestamp" to LocalDateTime.now(),
            "status" to 500,
            "error" to "Internal Server Error",
            "message" to (ex.message ?: "Unexpected error"),
            "path" to request.requestURI
        )
        return ResponseEntity.status(500).body(body)
    }
}