package com.example.lab3.web.controller

import com.example.lab3.application.service.DishService
import com.example.lab3.web.dto.*
import com.example.lab3.web.exception.NotFoundException
import com.example.lab3.web.exception.ValidationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/dishes")
class DishController(
    private val service: DishService
) {

    @GetMapping
    fun listDishes(@RequestParam(required = false) namePart: String?): List<DishResponse> =
        service.list(namePart).map {
            DishResponse(it.id, it.name, it.description, it.price.toDouble(), it.isAvailable)
        }

    @PostMapping
    fun createDish(@RequestBody req: DishCreateRequest): ResponseEntity<DishResponse> {
        if (req.name.isBlank() || req.description.isBlank() || req.price < 0)
            throw ValidationException("Invalid dish data")

        val (dish, created) = service.createOrGet(
            req.name, req.description, req.price, req.isAvailable
        )

        val response = DishResponse(dish.id, dish.name, dish.description, dish.price.toDouble(), dish.isAvailable)

        return if (created)
            ResponseEntity.status(HttpStatus.CREATED).body(response)
        else
            ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    fun getDish(@PathVariable id: Long): DishResponse {
        val dish = service.getById(id) ?: throw NotFoundException("Dish with id=$id not found")
        return DishResponse(dish.id, dish.name, dish.description, dish.price.toDouble(), dish.isAvailable)
    }

    @PutMapping("/{id}")
    fun updateDish(@PathVariable id: Long, @RequestBody req: DishUpdateRequest): DishResponse {
        val dish = service.update(id, req.name, req.description, req.price, req.isAvailable)
        return DishResponse(dish.id, dish.name, dish.description, dish.price.toDouble(), dish.isAvailable)
    }

    @DeleteMapping("/{id}")
    fun deleteDish(@PathVariable id: Long): ResponseEntity<Void> {
        if (!service.delete(id))
            throw NotFoundException("Dish with id=$id not found")
        return ResponseEntity.noContent().build()
    }
}