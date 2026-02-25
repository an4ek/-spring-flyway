package com.example.lab3.web.exception

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class RestExceptionHandler {

    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(ex: Exception, request: HttpServletRequest): ResponseEntity<Map<String, Any>> {
        val (status, errorMessage) = if (ex.message?.contains("обязательны") == true) {
            HttpStatus.BAD_REQUEST to ex.message!!
        } else {
            HttpStatus.INTERNAL_SERVER_ERROR to (ex.message ?: "Unexpected error")
        }

        val body = mapOf(
            "status" to status.value(),
            "error" to status.reasonPhrase,
            "message" to errorMessage,
            "path" to request.requestURI
        )

        return ResponseEntity.status(status).body(body)
    }
}