package com.example.lab3.infrastructure.mock

import com.example.lab3.domain.model.Dish
import com.example.lab3.domain.port.DishRepositoryPort
import org.springframework.context.annotation.Conditional
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

@Repository
@Profile("mock")
class DishMockRepository : DishRepositoryPort {

    private val storage = ConcurrentHashMap<Long, Dish>()
    private val seq = AtomicLong(1)

    override fun findAll(namePart: String?): List<Dish> {
        val all = storage.values.toList()
        return if (namePart.isNullOrBlank()) all
        else all.filter { it.name.contains(namePart, true) }
    }

    override fun findById(id: Long): Dish? = storage[id]

    override fun findByName(name: String): Dish? =
        storage.values.find { it.name.equals(name, true) }

    override fun create(dish: Dish): Dish {
        val id = seq.getAndIncrement()
        val saved = dish.copy(id = id)
        storage[id] = saved
        return saved
    }

    override fun update(dish: Dish): Dish {
        storage[dish.id] = dish
        return dish
    }

    override fun delete(id: Long): Boolean =
        storage.remove(id) != null
}