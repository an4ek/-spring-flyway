package com.example.lab3.web.controller

import com.example.lab3.application.service.RestaurantService
import com.example.lab3.domain.model.Dish
import com.example.lab3.domain.model.Restaurant
import com.example.lab3.web.dto.DishCreateRequest
import com.example.lab3.web.dto.RestaurantCreateRequest
import com.example.lab3.web.dto.RestaurantUpdateRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/restaurants")
class RestaurantController(private val restaurantService: RestaurantService) {

    @GetMapping
    fun getAll(): ResponseEntity<List<Restaurant>> = ResponseEntity.ok(restaurantService.findAll())

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<Restaurant> =
        ResponseEntity.ok(restaurantService.findById(id))

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun create(@Valid @RequestBody request: RestaurantCreateRequest): ResponseEntity<Restaurant> {
        val restaurant = Restaurant(0, request.name, request.address)
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurantService.create(restaurant))
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun update(@PathVariable id: Long, @Valid @RequestBody request: RestaurantUpdateRequest): ResponseEntity<Restaurant> {
        val restaurant = Restaurant(id, request.name, request.address)
        return ResponseEntity.ok(restaurantService.update(id, restaurant))
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        restaurantService.delete(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{id}/dishes")
    fun getDishes(@PathVariable id: Long): ResponseEntity<List<Dish>> =
        ResponseEntity.ok(restaurantService.getDishes(id))

    @PostMapping("/{restaurantId}/dishes")
    @PreAuthorize("hasRole('ADMIN')")
    fun addDish(
        @PathVariable restaurantId: Long,
        @Valid @RequestBody request: DishCreateRequest
    ): ResponseEntity<Dish> {
        val dish = Dish(0, request.name ?: "", request.description ?: "", request.price ?: 0.0, request.isAvailable, restaurantId)
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurantService.addDish(restaurantId, dish))
    }
}
