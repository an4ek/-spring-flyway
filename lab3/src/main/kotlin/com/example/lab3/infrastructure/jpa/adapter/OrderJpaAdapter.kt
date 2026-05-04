package com.example.lab3.infrastructure.jpa.adapter

import com.example.lab3.domain.model.Dish
import com.example.lab3.domain.model.Order
import com.example.lab3.domain.model.OrderStatus
import com.example.lab3.domain.port.OrderRepositoryPort
import com.example.lab3.infrastructure.jpa.entity.DishEntity
import com.example.lab3.infrastructure.jpa.entity.OrderEntity
import com.example.lab3.infrastructure.jpa.repository.DishJpaRepository
import com.example.lab3.infrastructure.jpa.repository.OrderJpaRepository
import com.example.lab3.infrastructure.jpa.repository.UserJpaRepository
import org.springframework.stereotype.Component

@Component
class OrderJpaAdapter(
    private val orderJpaRepository: OrderJpaRepository,
    private val userJpaRepository: UserJpaRepository,
    private val dishJpaRepository: DishJpaRepository
) : OrderRepositoryPort {

    private fun DishEntity.toDomain() = Dish(
        id = id, name = name, description = description,
        price = price, isAvailable = isAvailable,
        restaurantId = restaurant?.id
    )

    private fun OrderEntity.toDomain() = Order(
        id = id,
        userId = user.id,
        status = OrderStatus.valueOf(status),
        createdAt = createdAt,
        dishes = dishes.map { it.toDomain() }
    )

    override fun findAll(userId: Long?, status: OrderStatus?): List<Order> {
        return when {
            userId != null && status != null ->
                orderJpaRepository.findAllByUserIdAndStatus(userId, status.name)
            userId != null ->
                orderJpaRepository.findAllByUserId(userId)
            status != null ->
                orderJpaRepository.findAllByStatus(status.name)
            else ->
                orderJpaRepository.findAllWithDetails()
        }.map { it.toDomain() }
    }

    override fun findById(id: Long): Order? =
        orderJpaRepository.findByIdWithDetails(id)?.toDomain()

    override fun create(order: Order): Order {
        val user = userJpaRepository.findById(order.userId).orElseThrow {
            IllegalArgumentException("User with id=${order.userId} not found")
        }
        val dishes = dishJpaRepository.findAllByIdIn(order.dishes.map { it.id })
        val entity = OrderEntity(
            user = user,
            status = order.status.name,
            createdAt = order.createdAt,
            dishes = dishes.toMutableList()
        )
        return orderJpaRepository.save(entity).toDomain()
    }

    override fun updateStatus(id: Long, status: OrderStatus): Order {
        val entity = orderJpaRepository.findById(id).orElseThrow {
            RuntimeException("NOT_FOUND")
        }
        entity.status = status.name
        return orderJpaRepository.save(entity).toDomain()
    }
}