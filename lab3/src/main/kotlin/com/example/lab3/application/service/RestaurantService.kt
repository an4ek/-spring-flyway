package com.example.lab3.application.service

import com.example.lab3.domain.model.Dish
import com.example.lab3.domain.model.Restaurant
import com.example.lab3.domain.port.RestaurantRepositoryPort
import com.example.lab3.web.exception.AlreadyExistsException
import com.example.lab3.web.exception.NotFoundException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

@Service
class RestaurantService(private val restaurantRepository: RestaurantRepositoryPort) {

    private val logger = KotlinLogging.logger {}

    fun findAll(): List<Restaurant> = restaurantRepository.findAll()

    fun findById(id: Long): Restaurant {
        return restaurantRepository.findById(id)
            ?: throw NotFoundException("Ресторан с id=$id не найден")
    }

    fun create(restaurant: Restaurant): Restaurant {
    val exists = restaurantRepository.existsByName(restaurant.name)
    logger.info { "Проверка дубликата '${restaurant.name}': exists=$exists" }
    if (exists) {
        throw AlreadyExistsException("Ресторан '${restaurant.name}' уже существует")
    }
    val created = restaurantRepository.create(restaurant)
    logger.info { "Создан ресторан: id=${created.id}, name=${created.name}" }
    return created
    }

    fun update(id: Long, restaurant: Restaurant): Restaurant {
        restaurantRepository.findById(id) ?: throw NotFoundException("Ресторан с id=$id не найден")
        val updated = restaurantRepository.update(restaurant.copy(id = id))
        logger.info { "Обновлён ресторан: id=$id" }
        return updated
    }

    fun delete(id: Long) {
        restaurantRepository.findById(id) ?: throw NotFoundException("Ресторан с id=$id не найден")
        restaurantRepository.delete(id)
        logger.info { "Удалён ресторан: id=$id" }
    }

    fun getDishes(id: Long): List<Dish> {
        restaurantRepository.findById(id) ?: throw NotFoundException("Ресторан с id=$id не найден")
        return restaurantRepository.findDishes(id)
    }

    fun addDish(restaurantId: Long, dish: Dish): Dish {
        restaurantRepository.findById(restaurantId) ?: throw NotFoundException("Ресторан с id=$restaurantId не найден")
        return restaurantRepository.addDish(restaurantId, dish)
    }
}