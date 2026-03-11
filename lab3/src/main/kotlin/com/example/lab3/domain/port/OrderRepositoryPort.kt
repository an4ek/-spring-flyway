package com.example.lab3.domain.port

import com.example.lab3.domain.model.Order
import com.example.lab3.domain.model.OrderStatus

interface OrderRepositoryPort {
    fun findAll(userId: Long?, status: OrderStatus?): List<Order>
    fun findById(id: Long): Order?
    fun create(order: Order): Order
    fun updateStatus(id: Long, status: OrderStatus): Order
}