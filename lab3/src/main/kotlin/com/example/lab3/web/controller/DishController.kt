package com.example.lab3.web.controller

import com.example.lab3.application.service.DishService
import com.example.lab3.domain.model.Dish
import com.example.lab3.web.dto.DishUpdateRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/dishes")
class DishController(private val dishService: DishService) {

    @GetMapping
    fun getAll(@RequestParam(required = false) namePart: String?): ResponseEntity<List<Dish>> =
        if (namePart.isNullOrBlank()) ResponseEntity.ok(dishService.findAll())
        else ResponseEntity.ok(dishService.searchByName(namePart))

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<Dish> =
        ResponseEntity.ok(dishService.findById(id))

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @Valid @RequestBody request: DishUpdateRequest): ResponseEntity<Dish> {
        val dish = Dish(id, request.name ?: "", request.description ?: "", request.price ?: 0.0, request.isAvailable, 0)
        return ResponseEntity.ok(dishService.update(id, dish))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        dishService.delete(id)
        return ResponseEntity.noContent().build()
    }
}