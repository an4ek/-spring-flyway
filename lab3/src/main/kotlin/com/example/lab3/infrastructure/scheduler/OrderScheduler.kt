package com.example.lab3.infrastructure.scheduler

import com.example.lab3.application.service.NotificationService
import com.example.lab3.domain.model.OrderStatus
import com.example.lab3.domain.port.OrderRepositoryPort
import com.example.lab3.domain.port.UserRepositoryPort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class OrderScheduler(
    private val orderRepository: OrderRepositoryPort,
    private val userRepository: UserRepositoryPort,
    private val notificationService: NotificationService
) {
    private val logger = KotlinLogging.logger {}

    @Value("\${app.scheduler.stuck-order-threshold-hours:1}")
    private var thresholdHours: Long = 1

    @Scheduled(fixedDelayString = "\${app.scheduler.stuck-order-interval-ms:60000}")
    fun cancelStuckOrders() {
        val threshold = LocalDateTime.now().minusHours(thresholdHours)
        val stuckOrders = orderRepository.findByStatusAndCreatedBefore(
            OrderStatus.PENDING, threshold
        )
        logger.info { "Планировщик: найдено ${stuckOrders.size} зависших заказов" }

        stuckOrders.forEach { order ->
            orderRepository.updateStatus(order.id, OrderStatus.CANCELLED)
            logger.info { "Заказ id=${order.id} отменён планировщиком" }

            val user = userRepository.findById(order.userId)
            if (user != null) {
                notificationService.sendOrderStatusUpdate(
                    user.email, order.id, OrderStatus.CANCELLED.name
                )
            }
        }
    }
}
