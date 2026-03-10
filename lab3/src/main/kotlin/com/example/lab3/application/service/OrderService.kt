package com.example.lab3.application.service

import com.example.lab3.domain.model.Dish
import com.example.lab3.domain.model.Order
import com.example.lab3.domain.model.OrderStatus
import com.example.lab3.domain.port.DishRepositoryPort
import com.example.lab3.domain.port.OrderRepositoryPort
import com.example.lab3.domain.port.UserRepositoryPort
import org.springframework.stereotype.Service

@Service
class OrderService(
    private val orderRepository: OrderRepositoryPort,
    private val userRepository: UserRepositoryPort,
    private val dishRepository: DishRepositoryPort
) {
    private val allowedTransitions = mapOf(
        OrderStatus.PENDING to setOf(OrderStatus.CONFIRMED, OrderStatus.CANCELLED),
        OrderStatus.CONFIRMED to setOf(OrderStatus.DELIVERED, OrderStatus.CANCELLED),
        OrderStatus.DELIVERED to emptySet(),
        OrderStatus.CANCELLED to emptySet()
    )

    fun findAll(userId: Long?, status: OrderStatus?): List<Order> =
        orderRepository.findAll(userId, status)

    fun findById(id: Long): Order? = orderRepository.findById(id)

    fun create(userId: Long, dishIds: List<Long>): Order {
        userRepository.findById(userId)
            ?: throw IllegalArgumentException("User with id=$userId not found")

        if (dishIds.isEmpty()) throw IllegalArgumentException("dishIds must not be empty")

        val dishes = dishRepository.findAllByIds(dishIds)
        if (dishes.size != dishIds.size) {
            throw IllegalArgumentException("Some dishes not found")
        }

        val order = Order(userId = userId, dishes = dishes)
        return orderRepository.create(order)
    }

    fun updateStatus(id: Long, status: OrderStatus): Order {
        val existing = orderRepository.findById(id)
            ?: throw RuntimeException("NOT_FOUND")

        val allowed = allowedTransitions[existing.status] ?: emptySet()
        if (status !in allowed) {
            throw IllegalArgumentException(
                "Invalid status transition: ${existing.status} -> $status"
            )
        }

        return orderRepository.updateStatus(id, status)
    }
}