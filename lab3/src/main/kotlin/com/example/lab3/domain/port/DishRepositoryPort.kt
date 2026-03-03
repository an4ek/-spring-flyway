package com.example.lab3.domain.port

import com.example.lab3.domain.model.Dish

interface DishRepositoryPort {
    fun findAll(): List<Dish>
    fun searchByName(namePart: String): List<Dish>
    fun findById(id: Long): Dish?
    fun findAllByIds(ids: List<Long>): List<Dish>
    fun create(dish: Dish): Dish
    fun update(dish: Dish): Dish
    fun delete(id: Long): Boolean
}