package com.example.lab3.web.controller

import com.example.lab3.application.service.OrderService
import com.example.lab3.domain.model.Order
import com.example.lab3.domain.model.OrderStatus
import com.example.lab3.web.dto.OrderCreateRequest
import com.example.lab3.web.dto.OrderStatusUpdateRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/orders")
class OrderController(private val orderService: OrderService) {

    @GetMapping
    fun getAll(
        @RequestParam(required = false) userId: Long?,
        @RequestParam(required = false) status: OrderStatus?
    ): ResponseEntity<List<Order>> = ResponseEntity.ok(orderService.findAll(userId, status))

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<Order> =
        ResponseEntity.ok(orderService.findById(id))

    @PostMapping
    fun create(@Valid @RequestBody request: OrderCreateRequest): ResponseEntity<Order> {
        val order = orderService.create(request.userId ?: 0L, request.dishIds ?: emptyList())
        return ResponseEntity.status(HttpStatus.CREATED).body(order)
    }

    @PatchMapping("/{id}/status")
    fun updateStatus(
        @PathVariable id: Long,
        @Valid @RequestBody request: OrderStatusUpdateRequest
    ): ResponseEntity<Order> = ResponseEntity.ok(orderService.updateStatus(id, request.status ?: OrderStatus.PENDING))
}