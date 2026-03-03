package com.example.lab3.infrastructure.jpa.entity

import com.example.lab3.domain.model.User
import jakarta.persistence.*

@Entity
@Table(name = "users")
class UserEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = false)
    var firstName: String,

    @Column(nullable = false)
    var lastName: String,

    @Column(nullable = false)
    var isActive: Boolean = true
) {
    fun toDomain(): User = User(id, email, firstName, lastName, isActive)
}