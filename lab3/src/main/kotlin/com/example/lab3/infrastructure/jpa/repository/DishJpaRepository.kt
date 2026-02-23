package com.example.lab3.infrastructure.jpa.repository

import com.example.lab3.infrastructure.jpa.entity.DishEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface DishJpaRepository : JpaRepository<DishEntity, Long> {

    fun findByName(name: String): DishEntity?

    @Query("""
        select d
        from DishEntity d
        where d.isAvailable = true
          and (:namePart is null or lower(d.name) like lower(concat('%', :namePart, '%')))
        order by d.price asc
    """)
    fun findAvailableByNamePart(@Param("namePart") namePart: String?): List<DishEntity>
}