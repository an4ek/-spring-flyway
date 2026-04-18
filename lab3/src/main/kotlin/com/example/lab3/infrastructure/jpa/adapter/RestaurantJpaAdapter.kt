package com.example.lab3.infrastructure.jpa.adapter

import com.example.lab3.domain.model.Dish
import com.example.lab3.domain.model.Restaurant
import com.example.lab3.domain.port.RestaurantRepositoryPort
import com.example.lab3.infrastructure.jpa.entity.DishEntity
import com.example.lab3.infrastructure.jpa.entity.RestaurantEntity
import com.example.lab3.infrastructure.jpa.repository.DishJpaRepository
import com.example.lab3.infrastructure.jpa.repository.RestaurantJpaRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class RestaurantJpaAdapter(
    private val restaurantJpaRepository: RestaurantJpaRepository,
    private val dishJpaRepository: DishJpaRepository
) : RestaurantRepositoryPort {

    private fun RestaurantEntity.toDomain() = Restaurant(id, name, address)

    private fun DishEntity.toDomain() = Dish(
        id = id, name = name, description = description,
        price = price, isAvailable = isAvailable,
        restaurantId = restaurant?.id
    )

    override fun findAll(): List<Restaurant> =
        restaurantJpaRepository.findAll().map { it.toDomain() }

    override fun findById(id: Long): Restaurant? =
        restaurantJpaRepository.findById(id).orElse(null)?.toDomain()

    override fun existsByName(name: String): Boolean =
    restaurantJpaRepository.findByNameIgnoreCase(name) != null

    override fun create(restaurant: Restaurant): Restaurant =
        restaurantJpaRepository.save(
            RestaurantEntity(name = restaurant.name, address = restaurant.address)
        ).toDomain()

    override fun update(restaurant: Restaurant): Restaurant {
    val entity = restaurantJpaRepository.findById(restaurant.id).orElseThrow {
        RuntimeException("NOT_FOUND")
    }
    entity.address = restaurant.address
    restaurantJpaRepository.save(entity)
    return restaurant
    }

    override fun delete(id: Long): Boolean =
        restaurantJpaRepository.findById(id).map {
            restaurantJpaRepository.delete(it); true
        }.orElse(false)

    override fun findDishes(restaurantId: Long): List<Dish> {
        val entity = restaurantJpaRepository.findWithDishesById(restaurantId)
            ?: return emptyList()
        return entity.dishes.map { it.toDomain() }
    }

    override fun addDish(restaurantId: Long, dish: Dish): Dish {
        val restaurant = restaurantJpaRepository.findById(restaurantId).orElseThrow {
            RuntimeException("NOT_FOUND")
        }
        val dishEntity = DishEntity(
            name = dish.name,
            description = dish.description,
            price = dish.price,
            isAvailable = dish.isAvailable,
            restaurant = restaurant
        )
        val saved = dishJpaRepository.saveAndFlush(dishEntity)
        return saved.toDomain()
    }
}