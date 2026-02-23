package com.example.lab3.infrastructure.jpa.adapter

import com.example.lab3.domain.port.DishRepositoryPort
import com.example.lab3.domain.model.Dish
import com.example.lab3.infrastructure.jpa.entity.DishEntity
import com.example.lab3.infrastructure.jpa.repository.DishJpaRepository
import org.springframework.stereotype.Component

@Component
class DishJpaAdapter(
    private val dishJpaRepository: DishJpaRepository
) : DishRepositoryPort {

    override fun create(dish: Dish): Dish {
        val existing = dishJpaRepository.findByName(dish.name)
        if (existing != null) return existing.toDomain()
        return dishJpaRepository.save(DishEntity.fromDomain(dish)).toDomain()
    }

    override fun findById(id: Long): Dish? =
        dishJpaRepository.findById(id).orElse(null)?.toDomain()

    override fun findByName(name: String): Dish? =
        dishJpaRepository.findByName(name)?.toDomain()

    override fun update(dish: Dish): Dish {
        require(dish.id != 0L) { "Dish id must not be 0 for update" }
        return dishJpaRepository.save(DishEntity.fromDomain(dish)).toDomain()
    }

    override fun delete(id: Long): Boolean {
        if (!dishJpaRepository.existsById(id)) return false
        dishJpaRepository.deleteById(id)
        return true
    }

    override fun findAll(namePart: String?): List<Dish> =
        dishJpaRepository.findAvailableByNamePart(namePart)
            .map { it.toDomain() }
}