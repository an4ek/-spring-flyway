package com.example.lab3.web.exception

import com.example.lab3.web.dto.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.Instant
import jakarta.servlet.http.HttpServletRequest

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(ex: NotFoundException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ErrorResponse(
                timestamp = Instant.now().toString(),
                status = HttpStatus.NOT_FOUND.value(),
                error = "Not Found",
                message = ex.message ?: "Not found",
                path = request.requestURI
            )
        )
    }

    @ExceptionHandler(ValidationException::class)
    fun handleValidation(ex: ValidationException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse(
                timestamp = Instant.now().toString(),
                status = HttpStatus.BAD_REQUEST.value(),
                error = "Bad Request",
                message = ex.message ?: "Invalid user data",
                path = request.requestURI
            )
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val errorMessage = ex.bindingResult.allErrors
            .firstOrNull()?.defaultMessage
            ?: "Invalid user data"

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse(
                timestamp = Instant.now().toString(),
                status = HttpStatus.BAD_REQUEST.value(),
                error = "Bad Request",
                message = errorMessage,
                path = request.requestURI
            )
        )
    }
}