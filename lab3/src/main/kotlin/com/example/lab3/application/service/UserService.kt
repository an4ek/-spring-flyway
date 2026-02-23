package com.example.lab3.application.service

import com.example.lab3.domain.model.User
import com.example.lab3.domain.port.UserRepositoryPort
import org.springframework.stereotype.Service

@Service
class UserService(
    private val repository: UserRepositoryPort
) {

    fun list(): List<User> = repository.findAll()

    fun getById(id: Long): User? = repository.findById(id)

    fun createOrGet(email: String, firstName: String, lastName: String, isActive: Boolean): Pair<User, Boolean> {
        val existing = repository.findByEmail(email)
        if (existing != null) return existing to false

        val created = repository.create(
            User(0, email, firstName, lastName, isActive)
        )
        return created to true
    }

    fun update(id: Long, email: String, firstName: String, lastName: String, isActive: Boolean): User {
        val existing = repository.findById(id) ?: throw RuntimeException("NOT_FOUND")
        return repository.update(existing.copy(email = email, firstName = firstName, lastName = lastName, isActive = isActive))
    }

    fun delete(id: Long): Boolean = repository.delete(id)
}