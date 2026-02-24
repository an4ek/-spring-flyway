package com.example.lab3.web.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

// DTO для ошибок — только message
data class SimpleErrorResponse(val message: String)

@RestControllerAdvice
class GlobalExceptionHandler {

    // 404 Not Found
    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(ex: NotFoundException): ResponseEntity<SimpleErrorResponse> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(SimpleErrorResponse(message = ex.message ?: "Not found"))
    }

    // Кастомные ошибки валидации
    @ExceptionHandler(ValidationException::class)
    fun handleValidation(ex: ValidationException): ResponseEntity<SimpleErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(SimpleErrorResponse(message = ex.message ?: "Invalid user data"))
    }

    // Ошибки валидации @Valid
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException): ResponseEntity<SimpleErrorResponse> {
        val errorMessage = ex.bindingResult.allErrors
            .firstOrNull()?.defaultMessage
            ?: "Invalid user data"
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(SimpleErrorResponse(message = errorMessage))
    }

    // Универсальный обработчик для IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(ex: IllegalArgumentException): ResponseEntity<SimpleErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(SimpleErrorResponse(message = ex.message ?: "Bad Request"))
    }
}