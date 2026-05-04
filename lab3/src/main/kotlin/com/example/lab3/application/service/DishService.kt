package com.example.lab3.application.service

import com.example.lab3.domain.model.Dish
import com.example.lab3.domain.port.DishRepositoryPort
import com.example.lab3.web.exception.NotFoundException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Service

@Service
class DishService(private val dishRepository: DishRepositoryPort) {
    private val logger = KotlinLogging.logger {}

    @Cacheable(cacheNames = ["dishes"])
    fun findAll(): List<Dish> {
        logger.info { "Загрузка всех блюд из БД" }
        return dishRepository.findAll()
    }

    @Cacheable(cacheNames = ["dishes"], key = "'search_' + #namePart")
    fun searchByName(namePart: String): List<Dish> {
        logger.info { "Поиск блюд по '$namePart' из БД" }
        return dishRepository.searchByName(namePart)
    }

    @Cacheable(cacheNames = ["dishes"], key = "'dish_' + #id")
    fun findById(id: Long): Dish {
        logger.info { "Загрузка блюда id=$id из БД" }
        return dishRepository.findById(id)
            ?: throw NotFoundException("Блюдо с id=$id не найдено")
    }

    @CacheEvict(cacheNames = ["dishes"], allEntries = true)
    fun createOrFind(dish: Dish): Pair<Dish, Boolean> {
        val existing = dishRepository.searchByName(dish.name)
            .firstOrNull { it.name.equals(dish.name, ignoreCase = true) }
        return if (existing != null) {
            Pair(existing, false)
        } else {
            val created = dishRepository.create(dish)
            logger.info { "Создано блюдо: id=${created.id}, name=${created.name}, кэш инвалидирован" }
            Pair(created, true)
        }
    }

    @CacheEvict(cacheNames = ["dishes"], allEntries = true)
    fun update(id: Long, dish: Dish): Dish {
        dishRepository.findById(id) ?: throw NotFoundException("Блюдо с id=$id не найдено")
        val updated = dishRepository.update(dish.copy(id = id))
        logger.info { "Обновлено блюдо: id=$id, кэш инвалидирован" }
        return updated
    }

    @Caching(evict = [
        CacheEvict(cacheNames = ["dishes"], allEntries = true),
        CacheEvict(cacheNames = ["restaurants"], allEntries = true)
    ])
    fun delete(id: Long) {
        dishRepository.findById(id) ?: throw NotFoundException("Блюдо с id=$id не найдено")
        dishRepository.delete(id)
        logger.info { "Удалено блюдо: id=$id, кэш инвалидирован" }
    }
}
