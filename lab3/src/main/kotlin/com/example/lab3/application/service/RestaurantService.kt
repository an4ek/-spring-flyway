package com.example.lab3.application.service

import com.example.lab3.domain.model.Dish
import com.example.lab3.domain.model.Restaurant
import com.example.lab3.domain.port.RestaurantRepositoryPort
import org.springframework.stereotype.Service

@Service
class RestaurantService(private val restaurantRepository: RestaurantRepositoryPort) {

    fun findAll(): List<Restaurant> = restaurantRepository.findAll()

    fun findById(id: Long): Restaurant? = restaurantRepository.findById(id)

    fun create(restaurant: Restaurant): Restaurant = restaurantRepository.create(restaurant)

    fun update(id: Long, restaurant: Restaurant): Restaurant {
        restaurantRepository.findById(id) ?: throw RuntimeException("NOT_FOUND")
        return restaurantRepository.update(restaurant.copy(id = id))
    }

    fun delete(id: Long): Boolean = restaurantRepository.delete(id)

    fun findDishes(restaurantId: Long): List<Dish> {
        restaurantRepository.findById(restaurantId) ?: throw RuntimeException("NOT_FOUND")
        return restaurantRepository.findDishes(restaurantId)
    }

    fun addDish(restaurantId: Long, dish: Dish): Dish {
        restaurantRepository.findById(restaurantId) ?: throw RuntimeException("NOT_FOUND")
        return restaurantRepository.addDish(restaurantId, dish)
    }
}