package com.example.lab3.web.controller

import com.example.lab3.application.service.UserService
import com.example.lab3.web.dto.*
import com.example.lab3.web.exception.NotFoundException
import com.example.lab3.web.exception.ValidationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import jakarta.validation.Valid

@RestController
@RequestMapping("/api/v1/users")
@Validated
class UserController(
    private val service: UserService
) {

    @GetMapping
    fun listUsers(): List<UserResponse> =
        service.list().map {
            UserResponse(it.id, it.email, it.firstName, it.lastName, it.isActive)
        }

    @PostMapping
    fun createUser(@Valid @RequestBody req: CreateUserRequest): ResponseEntity<UserResponse> {

        val (user, created) = service.createOrGet(
            req.email, req.firstName, req.lastName, req.isActive ?: true
        )

        val response = UserResponse(user.id, user.email, user.firstName, user.lastName, user.isActive)

        return if (created)
            ResponseEntity.status(HttpStatus.CREATED).body(response)
        else
            ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: Long): UserResponse {
        val user = service.getById(id) ?: throw NotFoundException("User with id=$id not found")
        return UserResponse(user.id, user.email, user.firstName, user.lastName, user.isActive)
    }

    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: Long, @Valid @RequestBody req: CreateUserRequest): UserResponse {
        val user = service.update(id, req.email, req.firstName, req.lastName, req.isActive ?: true)
        return UserResponse(user.id, user.email, user.firstName, user.lastName, user.isActive)
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Void> {
        if (!service.delete(id))
            throw NotFoundException("User with id=$id not found")
        return ResponseEntity.noContent().build()
    }
}