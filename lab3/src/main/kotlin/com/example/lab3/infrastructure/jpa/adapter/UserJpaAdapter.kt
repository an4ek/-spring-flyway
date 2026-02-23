package com.example.lab3.infrastructure.jpa.adapter

import com.example.lab3.domain.model.User
import com.example.lab3.domain.port.UserRepositoryPort
import com.example.lab3.infrastructure.jpa.entity.UserEntity
import com.example.lab3.infrastructure.jpa.repository.UserJpaRepository
import org.springframework.stereotype.Component

@Component
class UserJpaAdapter(
    private val userJpaRepository: UserJpaRepository
) : UserRepositoryPort {

    override fun findAll(): List<User> =
        userJpaRepository.findAll().map { it.toDomain() }

    override fun findById(id: Long): User? =
        userJpaRepository.findById(id).orElse(null)?.toDomain()

    override fun findByEmail(email: String): User? =
        userJpaRepository.findByEmail(email)?.toDomain()

    override fun create(user: User): User {
        val saved = userJpaRepository.save(UserEntity.fromDomain(user))
        return saved.toDomain()
    }

    override fun update(user: User): User {
        require(user.id != 0L) { "User id must not be 0 for update" }
        val updated = userJpaRepository.save(UserEntity.fromDomain(user))
        return updated.toDomain()
    }

    override fun delete(id: Long): Boolean {
        if (!userJpaRepository.existsById(id)) return false
        userJpaRepository.deleteById(id)
        return true
    }
}