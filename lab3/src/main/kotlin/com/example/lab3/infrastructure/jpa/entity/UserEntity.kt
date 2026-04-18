package com.example.lab3.infrastructure.jpa.entity

import com.example.lab3.domain.model.Role
import com.example.lab3.domain.model.User
import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(name = "users")
class UserEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false, unique = true)
    var email: String = "",

    @Column(nullable = false)
    var firstName: String = "",

    @Column(nullable = false)
    var lastName: String = "",

    @Column(nullable = false)
    var isActive: Boolean = true,

    @Column(name = "password", nullable = false)
    var hashedPassword: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: Role = Role.USER
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> =
        listOf(SimpleGrantedAuthority("ROLE_${role.name}"))

    override fun getPassword(): String = hashedPassword

    override fun getUsername(): String = email

    fun toDomain(): User = User(id, email, firstName, lastName, isActive, role)
}
