package com.example.lab3.application.service

import com.example.lab3.domain.model.Dish
import com.example.lab3.domain.port.DishRepositoryPort
import com.example.lab3.web.exception.NotFoundException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

@Service
class DishService(private val dishRepository: DishRepositoryPort) {

    private val logger = KotlinLogging.logger {}

    fun findAll(): List<Dish> = dishRepository.findAll()

    fun searchByName(namePart: String): List<Dish> = dishRepository.searchByName(namePart)

    fun findById(id: Long): Dish {
        return dishRepository.findById(id)
            ?: throw NotFoundException("Блюдо с id=$id не найдено")
    }

    fun createOrFind(dish: Dish): Pair<Dish, Boolean> {
        val existing = dishRepository.searchByName(dish.name)
            .firstOrNull { it.name.equals(dish.name, ignoreCase = true) }
        return if (existing != null) {
            Pair(existing, false)
        } else {
            val created = dishRepository.create(dish)
            logger.info { "Создано блюдо: id=${created.id}, name=${created.name}" }
            Pair(created, true)
        }
    }

    fun update(id: Long, dish: Dish): Dish {
        dishRepository.findById(id) ?: throw NotFoundException("Блюдо с id=$id не найдено")
        val updated = dishRepository.update(dish.copy(id = id))
        logger.info { "Обновлено блюдо: id=$id" }
        return updated
    }

    fun delete(id: Long) {
        dishRepository.findById(id) ?: throw NotFoundException("Блюдо с id=$id не найдено")
        dishRepository.delete(id)
        logger.info { "Удалено блюдо: id=$id" }
    }
}