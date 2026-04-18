package com.example.lab3.web.controller

import com.example.lab3.application.service.OrderService
import com.example.lab3.domain.model.Order
import com.example.lab3.domain.model.OrderStatus
import com.example.lab3.infrastructure.jpa.entity.UserEntity
import com.example.lab3.web.dto.OrderCreateRequest
import com.example.lab3.web.dto.OrderStatusUpdateRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/orders")
class OrderController(private val orderService: OrderService) {

    @GetMapping
    fun getAll(
        @RequestParam(required = false) userId: Long?,
        @RequestParam(required = false) status: OrderStatus?,
        @AuthenticationPrincipal user: UserEntity
    ): ResponseEntity<List<Order>> {
        return if (user.role.name == "ADMIN") {
            ResponseEntity.ok(orderService.findAll(userId, status))
        } else {
            ResponseEntity.ok(orderService.findAll(user.id, status))
        }
    }

    @GetMapping("/{id}")
    fun getById(
        @PathVariable id: Long,
        @AuthenticationPrincipal user: UserEntity
    ): ResponseEntity<Order> {
        val order = orderService.findById(id)
        if (user.role.name != "ADMIN" && order.userId != user.id) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        return ResponseEntity.ok(order)
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    fun create(
        @Valid @RequestBody request: OrderCreateRequest,
        @AuthenticationPrincipal user: UserEntity
    ): ResponseEntity<Order> {
        val order = orderService.create(user.id, request.dishIds ?: emptyList())
        return ResponseEntity.status(HttpStatus.CREATED).body(order)
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateStatus(
        @PathVariable id: Long,
        @Valid @RequestBody request: OrderStatusUpdateRequest
    ): ResponseEntity<Order> = ResponseEntity.ok(
        orderService.updateStatus(id, request.status ?: OrderStatus.PENDING)
    )
}
