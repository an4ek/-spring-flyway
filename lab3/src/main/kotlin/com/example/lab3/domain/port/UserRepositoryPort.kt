package com.example.lab3.domain.port

import com.example.lab3.domain.model.User

interface UserRepositoryPort {
    fun findAll(): List<User>
    fun findById(id: Long): User?
    fun findByEmail(email: String): User?
    fun create(user: User): User
    fun update(user: User): User
    fun delete(id: Long): Boolean
}