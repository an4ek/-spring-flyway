package com.example.lab3.infrastructure.jpa.repository

import com.example.lab3.infrastructure.jpa.entity.RestaurantEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface RestaurantJpaRepository : JpaRepository<RestaurantEntity, Long> {
    @EntityGraph(attributePaths = ["dishes"])
    fun findWithDishesById(id: Long): RestaurantEntity?

    fun findByNameIgnoreCase(name: String): RestaurantEntity?
}