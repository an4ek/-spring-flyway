package com.example.lab3.web.controller

import com.example.lab3.application.service.UserService
import com.example.lab3.domain.model.User
import com.example.lab3.web.dto.CreateUserRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService
) {

    @GetMapping
    fun list(): List<User> = userService.list()

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): ResponseEntity<Any> =
        userService.getById(id)?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("status" to 404, "error" to "Not Found", "message" to "User not found"))

    @PostMapping
    fun createUser(@RequestBody req: CreateUserRequest): ResponseEntity<Any> {
        if (req.email.isNullOrBlank() || req.firstName.isNullOrBlank() || req.lastName.isNullOrBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(mapOf("status" to 400, "error" to "Bad Request", "message" to "email, firstName и lastName обязательны"))
        }

        val (user, created) = userService.createOrGet(
            req.email, req.firstName, req.lastName, req.isActive
        )
        return ResponseEntity.status(if (created) 201 else 200).body(user)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody req: CreateUserRequest): ResponseEntity<Any> {
        if (req.email.isNullOrBlank() || req.firstName.isNullOrBlank() || req.lastName.isNullOrBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(mapOf("status" to 400, "error" to "Bad Request", "message" to "email, firstName и lastName обязательны"))
        }

        return try {
            val updated = userService.update(id, req.email, req.firstName, req.lastName, req.isActive)
            ResponseEntity.ok(updated)
        } catch (e: RuntimeException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("status" to 404, "error" to "Not Found", "message" to e.message))
        }
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Any> {
        return if (userService.delete(id))
            ResponseEntity.noContent().build()
        else
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("status" to 404, "error" to "Not Found", "message" to "User not found"))
    }
}