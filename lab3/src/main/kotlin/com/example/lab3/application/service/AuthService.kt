package com.example.lab3.application.service

import com.example.lab3.domain.model.Role
import com.example.lab3.infrastructure.jpa.entity.UserEntity
import com.example.lab3.infrastructure.jpa.repository.UserJpaRepository
import com.example.lab3.infrastructure.security.JwtService
import com.example.lab3.web.dto.AuthResponse
import com.example.lab3.web.dto.LoginRequest
import com.example.lab3.web.dto.RegisterRequest
import com.example.lab3.web.exception.AlreadyExistsException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userJpaRepository: UserJpaRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager
) {
    private val logger = KotlinLogging.logger {}

    fun register(request: RegisterRequest): AuthResponse {
        val email = requireNotNull(request.email) { "Email обязателен" }
        val password = requireNotNull(request.password) { "Пароль обязателен" }
        val name = requireNotNull(request.name) { "Имя обязательно" }

        if (userJpaRepository.existsByEmail(email)) {
            throw AlreadyExistsException("Пользователь с email $email уже существует")
        }

        val nameParts = name.trim().split(" ", limit = 2)
        val firstName = nameParts[0]
        val lastName = if (nameParts.size > 1) nameParts[1] else ""

        val encoded = passwordEncoder.encode(password).toString()

        val user = UserEntity(
            email = email,
            firstName = firstName,
            lastName = lastName,
            role = Role.USER,
            hashedPassword = encoded
        )

        val saved = userJpaRepository.save(user)
        logger.info { "Зарегистрирован пользователь: ${saved.email}" }

        val token = jwtService.generateToken(saved.email, saved.role.name)
        return AuthResponse(token, saved.email, saved.role.name)
    }

    fun login(request: LoginRequest): AuthResponse {
        val email = requireNotNull(request.email) { "Email обязателен" }
        val password = requireNotNull(request.password) { "Пароль обязателен" }

        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(email, password)
        )

        val user = userJpaRepository.findByEmail(email)!!
        logger.info { "Вход пользователя: ${user.email}" }

        val token = jwtService.generateToken(user.email, user.role.name)
        return AuthResponse(token, user.email, user.role.name)
    }
}
