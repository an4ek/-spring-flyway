package com.example.lab3.infrastructure.jpa.adapter

import com.example.lab3.domain.model.Dish
import com.example.lab3.domain.port.DishRepositoryPort
import com.example.lab3.infrastructure.jpa.entity.DishEntity
import com.example.lab3.infrastructure.jpa.repository.DishJpaRepository
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Primary
@Component
@Transactional
class DishJpaAdapter(
    private val dishJpaRepository: DishJpaRepository
) : DishRepositoryPort {

    private fun DishEntity.toDomain() = Dish(
        id = id, name = name, description = description,
        price = price, isAvailable = isAvailable,
        restaurantId = restaurant?.id
    )

    override fun findAll(): List<Dish> =
        dishJpaRepository.findAll().map { it.toDomain() }

    override fun searchByName(namePart: String): List<Dish> =
        dishJpaRepository.findByNameContainingIgnoreCase(namePart).map { it.toDomain() }

    override fun findById(id: Long): Dish? =
        dishJpaRepository.findById(id).orElse(null)?.toDomain()

    override fun findAllByIds(ids: List<Long>): List<Dish> =
        dishJpaRepository.findAllByIdIn(ids).map { it.toDomain() }

    override fun create(dish: Dish): Dish =
        dishJpaRepository.save(
            DishEntity(
                name = dish.name, description = dish.description,
                price = dish.price, isAvailable = dish.isAvailable
            )
        ).toDomain()

    override fun update(dish: Dish): Dish {
        val entity = dishJpaRepository.findById(dish.id).orElseThrow {
            RuntimeException("NOT_FOUND")
        }
        entity.name = dish.name
        entity.description = dish.description
        entity.price = dish.price
        entity.isAvailable = dish.isAvailable
        return dishJpaRepository.save(entity).toDomain()
    }

    override fun delete(id: Long): Boolean {
        val entity = dishJpaRepository.findById(id).orElse(null) ?: return false
        entity.orders.forEach { order -> order.dishes.remove(entity) }
        dishJpaRepository.delete(entity)
        return true
    }
}