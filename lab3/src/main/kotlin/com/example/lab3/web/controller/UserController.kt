package com.example.lab3.web.controller

import com.example.lab3.application.service.UserService
import com.example.lab3.domain.model.User
import com.example.lab3.web.dto.UserCreateRequest
import com.example.lab3.web.dto.UserUpdateRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/users")
class UserController(private val userService: UserService) {

    @GetMapping
    fun getAll(): ResponseEntity<List<User>> = ResponseEntity.ok(userService.findAll())

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<User> =
        ResponseEntity.ok(userService.findById(id))

    @PostMapping
fun create(@Valid @RequestBody request: UserCreateRequest): ResponseEntity<User> {
    val user = User(0, request.email.orEmpty(), request.firstName.orEmpty(), request.lastName.orEmpty(), request.isActive ?: true)
    val (result, created) = userService.createOrFind(user)
    return if (created) ResponseEntity.status(HttpStatus.CREATED).body(result)
    else ResponseEntity.ok(result)
}

    @PutMapping("/{id}")
fun update(@PathVariable id: Long, @Valid @RequestBody request: UserUpdateRequest): ResponseEntity<User> {
    val user = User(id, request.email.orEmpty(), request.firstName.orEmpty(), request.lastName.orEmpty(), request.isActive ?: true)
    return ResponseEntity.ok(userService.update(id, user))
}

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        userService.delete(id)
        return ResponseEntity.noContent().build()
    }
}