package com.example.lab3.infrastructure.jpa.adapter

import com.example.lab3.domain.model.User
import com.example.lab3.domain.port.UserRepositoryPort
import com.example.lab3.infrastructure.jpa.entity.UserEntity
import com.example.lab3.infrastructure.jpa.repository.OrderJpaRepository
import com.example.lab3.infrastructure.jpa.repository.UserJpaRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class UserJpaAdapter(
    private val repository: UserJpaRepository,
    private val orderJpaRepository: OrderJpaRepository
) : UserRepositoryPort {

    override fun findAll(): List<User> = repository.findAll().map { it.toDomain() }

    override fun findById(id: Long): User? = repository.findById(id).orElse(null)?.toDomain()

    override fun findByEmail(email: String): User? = repository.findByEmail(email)?.toDomain()

    override fun create(user: User): User = repository.save(user.toEntity()).toDomain()

    override fun update(user: User): User = repository.save(user.toEntity()).toDomain()

    override fun delete(id: Long): Boolean {
        if (!repository.existsById(id)) return false
        val orders = orderJpaRepository.findAllByUserId(id)
        orders.forEach { order ->
            order.dishes.clear()
            orderJpaRepository.save(order)
        }
        orderJpaRepository.deleteAll(orders)
        repository.deleteById(id)
        return true
    }

    private fun User.toEntity() = UserEntity(id, email, firstName, lastName, isActive)
}