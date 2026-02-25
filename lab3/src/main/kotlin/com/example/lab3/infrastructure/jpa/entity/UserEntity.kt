package com.example.lab3.infrastructure.jpa.entity

import com.example.lab3.domain.model.User
import jakarta.persistence.*

@Entity
@Table(name = "users")
data class UserEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val email: String,
    val firstName: String,
    val lastName: String,
    val isActive: Boolean = true
) {
    fun toDomain(): User = User(id, email, firstName, lastName, isActive)
}