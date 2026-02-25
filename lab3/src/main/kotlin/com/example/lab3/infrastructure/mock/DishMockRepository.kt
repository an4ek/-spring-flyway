package com.example.lab3.infrastructure.mock

import com.example.lab3.domain.model.Dish
import com.example.lab3.domain.port.DishRepositoryPort

class DishMockRepository : DishRepositoryPort {

    private val dishes = mutableListOf<Dish>()
    private var nextId = 1L

    override fun findAll(): List<Dish> = dishes.toList()

    override fun searchByName(namePart: String): List<Dish> =
        dishes.filter { it.name.contains(namePart, ignoreCase = true) }

    override fun findById(id: Long): Dish? =
        dishes.find { it.id == id }

    override fun create(dish: Dish): Dish {
        val newDish = dish.copy(id = nextId++)
        dishes.add(newDish)
        return newDish
    }

    override fun update(dish: Dish): Dish {
        val index = dishes.indexOfFirst { it.id == dish.id }
        if (index == -1) throw RuntimeException("Dish not found")
        dishes[index] = dish
        return dish
    }

    override fun delete(id: Long): Boolean =
        dishes.removeIf { it.id == id }
}