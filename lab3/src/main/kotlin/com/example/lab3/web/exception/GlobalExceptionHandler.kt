package com.example.lab3.web.exception

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = KotlinLogging.logger {}

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(e: NotFoundException): ResponseEntity<ErrorResponse> {
        logger.warn { "Not found: ${e.message}" }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(404, e.message))
    }

    @ExceptionHandler(AlreadyExistsException::class)
    fun handleConflict(e: AlreadyExistsException): ResponseEntity<ErrorResponse> {
        logger.warn { "Conflict: ${e.message}" }
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ErrorResponse(409, e.message))
    }

    @ExceptionHandler(InvalidOrderStateException::class)
    fun handleInvalidOrderState(e: InvalidOrderStateException): ResponseEntity<ErrorResponse> {
        logger.warn { "Invalid order state: ${e.message}" }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(400, e.message))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ValidationErrorResponse> {
        val errors = ex.bindingResult.fieldErrors.associate {
            it.field to (it.defaultMessage ?: "Invalid value")
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ValidationErrorResponse(400, "Validation error", errors))
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(e: ConstraintViolationException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(400, e.message))
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleInvalidJson(ex: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(400, "Invalid request body"))
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(e: Exception): ResponseEntity<ErrorResponse> {
        logger.error(e) { "Unexpected error" }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse(500, "Internal server error"))
    }
    @ExceptionHandler(IllegalArgumentException::class)
fun handleIllegalArgument(e: IllegalArgumentException): ResponseEntity<ErrorResponse> {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ErrorResponse(400, e.message))
}
}