package com.example.lab3.infrastructure.security

import com.example.lab3.infrastructure.jpa.repository.UserJpaRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userJpaRepository: UserJpaRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        return userJpaRepository.findByEmail(username)
            ?: throw UsernameNotFoundException("Пользователь не найден: $username")
    }
}
