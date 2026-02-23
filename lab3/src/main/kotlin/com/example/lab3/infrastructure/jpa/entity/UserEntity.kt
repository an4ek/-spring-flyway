package com.example.lab3.infrastructure.jpa.entity

import com.example.lab3.domain.model.User
import jakarta.persistence.*

@Entity
@Table(name = "users")
class UserEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    var email: String,

    var firstName: String,

    var lastName: String,

    var isActive: Boolean
) {

    fun toDomain(): User =
        User(
            id = this.id ?: 0,
            email = this.email,
            firstName = this.firstName,
            lastName = this.lastName,
            isActive = this.isActive
        )

    companion object {
        fun fromDomain(user: User): UserEntity =
            UserEntity(
                id = if (user.id == 0L) null else user.id,
                email = user.email,
                firstName = user.firstName,
                lastName = user.lastName,
                isActive = user.isActive
            )
    }
}