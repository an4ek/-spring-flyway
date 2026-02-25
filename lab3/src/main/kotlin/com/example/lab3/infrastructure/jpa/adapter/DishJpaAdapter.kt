package com.example.lab3.infrastructure.jpa.adapter

import com.example.lab3.domain.model.Dish
import com.example.lab3.domain.port.DishRepositoryPort
import com.example.lab3.infrastructure.jpa.entity.DishEntity
import com.example.lab3.infrastructure.jpa.repository.DishJpaRepository
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Primary
@Component
class DishJpaAdapter(
    private val dishJpaRepository: DishJpaRepository
) : DishRepositoryPort {

    private fun Dish.toEntity(): DishEntity = DishEntity(
        id = this.id.takeIf { it != 0L },
        name = this.name,
        description = this.description,
        price = this.price,
        isAvailable = this.isAvailable
    )

    private fun DishEntity.toDomain(): Dish = Dish(
        id = this.id ?: 0L,
        name = this.name,
        description = this.description,
        price = this.price,
        isAvailable = this.isAvailable
    )

    override fun findAll(): List<Dish> =
        dishJpaRepository.findAll().map { it.toDomain() }

    override fun searchByName(namePart: String): List<Dish> =
        dishJpaRepository.findByNameContainingIgnoreCase(namePart).map { it.toDomain() }

    override fun findById(id: Long): Dish? =
        dishJpaRepository.findById(id).orElse(null)?.toDomain()

    override fun create(dish: Dish): Dish =
        dishJpaRepository.save(dish.toEntity()).toDomain()

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

    override fun delete(id: Long): Boolean =
        dishJpaRepository.findById(id).map {
            dishJpaRepository.delete(it)
            true
        }.orElse(false)
}