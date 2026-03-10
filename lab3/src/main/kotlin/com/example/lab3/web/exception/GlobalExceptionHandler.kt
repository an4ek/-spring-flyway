package com.example.lab3.web.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.MethodArgumentNotValidException

data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String
)

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleInvalidJson(ex: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.reasonPhrase, ex.message ?: "Invalid request body"),
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val message = ex.bindingResult.fieldErrors.joinToString { it.defaultMessage ?: it.field }
        return ResponseEntity(
            ErrorResponse(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.reasonPhrase, message),
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.reasonPhrase, ex.message ?: "Invalid request"),
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(com.example.lab3.web.exception.NotFoundException::class)
    fun handleNotFound(ex: com.example.lab3.web.exception.NotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.reasonPhrase, ex.message ?: "Not found"),
            HttpStatus.NOT_FOUND
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleOther(ex: Exception): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase, ex.message ?: "Internal server error"),
            HttpStatus.INTERNAL_SERVER_ERROR
        )
    }
}