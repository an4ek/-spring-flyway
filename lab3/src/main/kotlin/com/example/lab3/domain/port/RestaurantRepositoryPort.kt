package com.example.lab3.domain.port

import com.example.lab3.domain.model.Dish
import com.example.lab3.domain.model.Restaurant

interface RestaurantRepositoryPort {
    fun findAll(): List<Restaurant>
    fun findById(id: Long): Restaurant?
    fun existsByName(name: String): Boolean
    fun create(restaurant: Restaurant): Restaurant
    fun update(restaurant: Restaurant): Restaurant
    fun delete(id: Long): Boolean
    fun findDishes(restaurantId: Long): List<Dish>
    fun addDish(restaurantId: Long, dish: Dish): Dish
}