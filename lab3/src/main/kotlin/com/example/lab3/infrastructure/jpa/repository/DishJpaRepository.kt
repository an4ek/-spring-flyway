package com.example.lab3.infrastructure.jpa.repository

import com.example.lab3.infrastructure.jpa.entity.DishEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface DishJpaRepository : JpaRepository<DishEntity, Long> {
    fun findByNameContainingIgnoreCase(name: String): List<DishEntity>

    @Query("select d from DishEntity d where lower(d.name) like lower(concat('%', :namePart, '%'))")
    fun searchByNameJpql(namePart: String): List<DishEntity>
}