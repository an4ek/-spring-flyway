package com.example.lab3.application.service

import com.example.lab3.domain.model.Order
import com.example.lab3.domain.model.OrderStatus
import com.example.lab3.domain.port.DishRepositoryPort
import com.example.lab3.domain.port.OrderRepositoryPort
import com.example.lab3.domain.port.UserRepositoryPort
import com.example.lab3.web.exception.InvalidOrderStateException
import com.example.lab3.web.exception.NotFoundException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

@Service
class OrderService(
    private val orderRepository: OrderRepositoryPort,
    private val userRepository: UserRepositoryPort,
    private val dishRepository: DishRepositoryPort
) {

    private val logger = KotlinLogging.logger {}

    private val allowedTransitions = mapOf(
        OrderStatus.PENDING to setOf(OrderStatus.CONFIRMED, OrderStatus.CANCELLED),
        OrderStatus.CONFIRMED to setOf(OrderStatus.DELIVERED, OrderStatus.CANCELLED),
        OrderStatus.DELIVERED to emptySet(),
        OrderStatus.CANCELLED to emptySet()
    )

    fun findAll(userId: Long? = null, status: OrderStatus? = null): List<Order> =
        orderRepository.findAll(userId, status)

    fun findById(id: Long): Order {
        return orderRepository.findById(id)
            ?: throw NotFoundException("Заказ с id=$id не найден")
    }

    fun create(userId: Long, dishIds: List<Long>): Order {
    userRepository.findById(userId)
        ?: throw IllegalArgumentException("Пользователь с id=$userId не найден")
    val dishes = dishIds.map { dishId ->
        dishRepository.findById(dishId)
            ?: throw IllegalArgumentException("Блюдо с id=$dishId не найдено")
    }
    val order = Order(
        id = 0,
        userId = userId,
        status = com.example.lab3.domain.model.OrderStatus.PENDING,
        createdAt = java.time.LocalDateTime.now(),
        dishes = dishes
    )
    val created = orderRepository.create(order)
    logger.info { "Создан заказ: id=${created.id}, userId=$userId" }
    return created
}

    fun updateStatus(id: Long, newStatus: OrderStatus): Order {
        val order = orderRepository.findById(id)
            ?: throw NotFoundException("Заказ с id=$id не найден")
        val allowed = allowedTransitions[order.status] ?: emptySet()
        if (newStatus !in allowed) {
            throw InvalidOrderStateException(
                "Нельзя перевести заказ из ${order.status} в $newStatus"
            )
        }
        val updated = orderRepository.updateStatus(id, newStatus)
        logger.info { "Статус заказа id=$id изменён: ${order.status} -> $newStatus" }
        return updated
    }
}