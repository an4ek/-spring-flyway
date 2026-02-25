package com.example.lab3.application.service

import com.example.lab3.domain.model.Dish
import com.example.lab3.domain.port.DishRepositoryPort
import org.springframework.stereotype.Service

@Service
class DishService(private val dishRepository: DishRepositoryPort) {

    fun findAll(): List<Dish> = dishRepository.findAll()

    fun searchByName(namePart: String): List<Dish> = dishRepository.searchByName(namePart)

    fun findById(id: Long): Dish? = dishRepository.findById(id)

    fun createOrFind(dish: Dish): Pair<Dish, Boolean> {
        val existing = dishRepository.searchByName(dish.name)
            .firstOrNull { it.name.equals(dish.name, ignoreCase = true) }
        return if (existing != null) {
            Pair(existing, false)
        } else {
            Pair(dishRepository.create(dish), true)
        }
    }

    fun create(dish: Dish): Dish = dishRepository.create(dish)

    fun update(id: Long, dish: Dish): Dish {
        val existing = dishRepository.findById(id) ?: throw RuntimeException("Dish not found")
        val updated = existing.copy(
            name = dish.name,
            description = dish.description,
            price = dish.price,
            isAvailable = dish.isAvailable
        )
        return dishRepository.update(updated)
    }

    fun delete(id: Long): Boolean = dishRepository.delete(id)
}