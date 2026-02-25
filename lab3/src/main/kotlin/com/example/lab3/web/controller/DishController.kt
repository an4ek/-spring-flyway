package com.example.lab3.web.controller

import com.example.lab3.application.service.DishService
import com.example.lab3.domain.model.Dish
import com.example.lab3.web.exception.ErrorResponse
import com.example.lab3.web.exception.NotFoundException
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
    fun getById(@PathVariable id: Long): ResponseEntity<Any> {
        val dish = dishService.findById(id)
        return if (dish != null) {
            ResponseEntity.ok(dish)
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErrorResponse(404, "Not Found", "Dish not found")
            )
        }
    }

    @PostMapping
    fun create(@RequestBody dish: Dish): ResponseEntity<Dish> {
        val (result, created) = dishService.createOrFind(dish)
        return if (created) {
            ResponseEntity.status(HttpStatus.CREATED).body(result)
        } else {
            ResponseEntity.ok(result)
        }
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody dish: Dish): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok(dishService.update(id, dish))
        } catch (ex: RuntimeException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErrorResponse(404, "Not Found", "Dish not found")
            )
        }
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Any> {
        return if (dishService.delete(id)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErrorResponse(404, "Not Found", "Dish not found")
            )
        }
    }
}