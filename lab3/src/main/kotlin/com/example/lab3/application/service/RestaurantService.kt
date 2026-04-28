package com.example.lab3.application.service

import com.example.lab3.domain.model.Dish
import com.example.lab3.domain.model.Restaurant
import com.example.lab3.domain.port.RestaurantRepositoryPort
import com.example.lab3.web.exception.AlreadyExistsException
import com.example.lab3.web.exception.NotFoundException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Service

@Service
class RestaurantService(private val restaurantRepository: RestaurantRepositoryPort) {
    private val logger = KotlinLogging.logger {}

    @Cacheable(cacheNames = ["restaurants"])
    fun findAll(): List<Restaurant> {
        logger.info { "Загрузка всех ресторанов из БД" }
        return restaurantRepository.findAll()
    }

    @Cacheable(cacheNames = ["restaurants"], key = "#id")
    fun findById(id: Long): Restaurant {
        logger.info { "Загрузка ресторана id=$id из БД" }
        return restaurantRepository.findById(id)
            ?: throw NotFoundException("Ресторан с id=$id не найден")
    }

    @CacheEvict(cacheNames = ["restaurants"], allEntries = true)
    fun create(restaurant: Restaurant): Restaurant {
        val exists = restaurantRepository.existsByName(restaurant.name)
        logger.info { "Проверка дубликата '${restaurant.name}': exists=$exists" }
        if (exists) {
            throw AlreadyExistsException("Ресторан '${restaurant.name}' уже существует")
        }
        val created = restaurantRepository.create(restaurant)
        logger.info { "Создан ресторан: id=${created.id}, name=${created.name}, кэш инвалидирован" }
        return created
    }

    @CacheEvict(cacheNames = ["restaurants"], allEntries = true)
    fun update(id: Long, restaurant: Restaurant): Restaurant {
        restaurantRepository.findById(id) ?: throw NotFoundException("Ресторан с id=$id не найден")
        val updated = restaurantRepository.update(restaurant.copy(id = id))
        logger.info { "Обновлён ресторан: id=$id, кэш инвалидирован" }
        return updated
    }

    @Caching(evict = [
        CacheEvict(cacheNames = ["restaurants"], allEntries = true),
        CacheEvict(cacheNames = ["dishes"], allEntries = true)
    ])
    fun delete(id: Long) {
        restaurantRepository.findById(id) ?: throw NotFoundException("Ресторан с id=$id не найден")
        restaurantRepository.delete(id)
        logger.info { "Удалён ресторан: id=$id, кэш инвалидирован" }
    }

    @Cacheable(cacheNames = ["dishes"], key = "#id")
    fun getDishes(id: Long): List<Dish> {
        logger.info { "Загрузка блюд ресторана id=$id из БД" }
        restaurantRepository.findById(id) ?: throw NotFoundException("Ресторан с id=$id не найден")
        return restaurantRepository.findDishes(id)
    }

    @Caching(evict = [
        CacheEvict(cacheNames = ["dishes"], allEntries = true),
        CacheEvict(cacheNames = ["restaurants"], allEntries = true)
    ])
    fun addDish(restaurantId: Long, dish: Dish): Dish {
        restaurantRepository.findById(restaurantId)
            ?: throw NotFoundException("Ресторан с id=$restaurantId не найден")
        val created = restaurantRepository.addDish(restaurantId, dish)
        logger.info { "Добавлено блюдо в ресторан id=$restaurantId, кэш инвалидирован" }
        return created
    }
}
