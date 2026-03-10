package com.example.lab3.web.controller

import com.example.lab3.application.service.OrderService
import com.example.lab3.domain.model.OrderStatus
import com.example.lab3.web.exception.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

data class OrderCreateRequest(val userId: Long, val dishIds: List<Long>)
data class OrderStatusUpdateRequest(val status: String)

@RestController
@RequestMapping("/api/v1/orders")
class OrderController(private val orderService: OrderService) {

    @GetMapping
    fun getAll(
        @RequestParam(required = false) userId: Long?,
        @RequestParam(required = false) status: String?
    ): ResponseEntity<Any> {
        val orderStatus = status?.let {
            try { OrderStatus.valueOf(it) }
            catch (e: IllegalArgumentException) {
                return ResponseEntity.badRequest().body(ErrorResponse(400, "Bad Request", "Invalid status: $it"))
            }
        }
        return ResponseEntity.ok(orderService.findAll(userId, orderStatus))
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<Any> {
        val order = orderService.findById(id)
            ?: return ResponseEntity.status(404).body(ErrorResponse(404, "Not Found", "Order with id=$id not found"))
        return ResponseEntity.ok(order)
    }

    @PostMapping
    fun create(@RequestBody req: OrderCreateRequest): ResponseEntity<Any> {
        return try {
            ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(req.userId, req.dishIds))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(ErrorResponse(400, "Bad Request", e.message ?: "Invalid request"))
        }
    }

    @PatchMapping("/{id}/status")
    fun updateStatus(@PathVariable id: Long, @RequestBody req: OrderStatusUpdateRequest): ResponseEntity<Any> {
        val status = try {
            OrderStatus.valueOf(req.status)
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.badRequest().body(ErrorResponse(400, "Bad Request", "Invalid status: ${req.status}"))
        }
        return try {
            ResponseEntity.ok(orderService.updateStatus(id, status))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(ErrorResponse(400, "Bad Request", e.message ?: "Invalid transition"))
        } catch (e: RuntimeException) {
            ResponseEntity.status(404).body(ErrorResponse(404, "Not Found", "Order with id=$id not found"))
        }
    }
}