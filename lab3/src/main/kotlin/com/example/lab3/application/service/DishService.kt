package com.example.lab3.application.service

import com.example.lab3.domain.model.Dish
import com.example.lab3.domain.port.DishRepositoryPort
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class DishService(
    private val repository: DishRepositoryPort
) {

    fun list(namePart: String?): List<Dish> = repository.findAll(namePart)

    fun getById(id: Long): Dish? = repository.findById(id)

    fun createOrGet(name: String, description: String, price: Double, isAvailable: Boolean): Pair<Dish, Boolean> {
        val existing = repository.findByName(name)
        if (existing != null) return existing to false

        val created = repository.create(
            Dish(0, name, description, BigDecimal.valueOf(price), isAvailable)
        )
        return created to true
    }

    fun update(id: Long, name: String, description: String, price: Double, isAvailable: Boolean): Dish {
        val existing = repository.findById(id) ?: throw RuntimeException("NOT_FOUND")
        return repository.update(
            existing.copy(
                name = name,
                description = description,
                price = BigDecimal.valueOf(price),
                isAvailable = isAvailable
            )
        )
    }

    fun delete(id: Long): Boolean = repository.delete(id)
}