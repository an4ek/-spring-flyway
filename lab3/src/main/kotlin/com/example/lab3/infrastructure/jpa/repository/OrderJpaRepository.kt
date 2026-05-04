package com.example.lab3.infrastructure.jpa.repository

import com.example.lab3.infrastructure.jpa.entity.OrderEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface OrderJpaRepository : JpaRepository<OrderEntity, Long> {
    @EntityGraph(attributePaths = ["user", "dishes", "dishes.restaurant"])
    @Query("select o from OrderEntity o")
    fun findAllWithDetails(): List<OrderEntity>

    @EntityGraph(attributePaths = ["user", "dishes", "dishes.restaurant"])
    @Query("select o from OrderEntity o where o.user.id = :userId")
    fun findAllByUserId(userId: Long): List<OrderEntity>

    @EntityGraph(attributePaths = ["user", "dishes", "dishes.restaurant"])
    @Query("select o from OrderEntity o where o.status = :status")
    fun findAllByStatus(status: String): List<OrderEntity>

    @EntityGraph(attributePaths = ["user", "dishes", "dishes.restaurant"])
    @Query("select o from OrderEntity o where o.user.id = :userId and o.status = :status")
    fun findAllByUserIdAndStatus(userId: Long, status: String): List<OrderEntity>

    @EntityGraph(attributePaths = ["user", "dishes", "dishes.restaurant"])
    @Query("select o from OrderEntity o where o.id = :id")
    fun findByIdWithDetails(id: Long): OrderEntity?
}