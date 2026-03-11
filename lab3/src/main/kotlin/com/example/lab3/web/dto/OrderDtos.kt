package com.example.lab3.web.dto

import com.example.lab3.domain.model.OrderStatus
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class OrderCreateRequest(
    @field:NotNull(message = "userId обязателен")
    val userId: Long?,

    @field:NotEmpty(message = "Заказ должен содержать хотя бы одно блюдо")
    val dishIds: List<Long>?
)

data class OrderStatusUpdateRequest(
    @field:NotNull(message = "Статус обязателен")
    val status: OrderStatus?
)