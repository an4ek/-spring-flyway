package com.example.lab3.application.service

import com.example.lab3.domain.model.User
import com.example.lab3.domain.port.UserRepositoryPort
import com.example.lab3.web.exception.NotFoundException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepositoryPort) {

    private val logger = KotlinLogging.logger {}

    fun findAll(): List<User> = userRepository.findAll()

    fun findById(id: Long): User {
        return userRepository.findById(id)
            ?: throw NotFoundException("Пользователь с id=$id не найден")
    }

    fun createOrFind(user: User): Pair<User, Boolean> {
        val existing = userRepository.findByEmail(user.email)
        return if (existing != null) {
            Pair(existing, false)
        } else {
            val created = userRepository.create(user)
            logger.info { "Создан пользователь: id=${created.id}, email=${created.email}" }
            Pair(created, true)
        }
    }

    fun update(id: Long, user: User): User {
        userRepository.findById(id) ?: throw NotFoundException("Пользователь с id=$id не найден")
        val updated = userRepository.update(user.copy(id = id))
        logger.info { "Обновлён пользователь: id=$id" }
        return updated
    }

    fun delete(id: Long) {
        userRepository.findById(id) ?: throw NotFoundException("Пользователь с id=$id не найден")
        userRepository.delete(id)
        logger.info { "Удалён пользователь: id=$id" }
    }
}