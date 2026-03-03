package com.example.lab3.web.controller

import com.example.lab3.application.service.RestaurantService
import com.example.lab3.domain.model.Dish
import com.example.lab3.domain.model.Restaurant
import com.example.lab3.web.exception.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

data class RestaurantCreateRequest(val name: String, val address: String)
data class DishCreateRequest(
    val name: String, val description: String,
    val price: Double, val isAvailable: Boolean
)

@RestController
@RequestMapping("/api/v1/restaurants")
class RestaurantController(private val restaurantService: RestaurantService) {

    @GetMapping
    fun getAll(): ResponseEntity<List<Restaurant>> =
        ResponseEntity.ok(restaurantService.findAll())

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<Any> {
        val r = restaurantService.findById(id)
            ?: return ResponseEntity.status(404).body(ErrorResponse(404, "Not Found", "Restaurant with id=$id not found"))
        return ResponseEntity.ok(r)
    }

    @PostMapping
    fun create(@RequestBody req: RestaurantCreateRequest): ResponseEntity<Any> {
        if (req.name.isBlank() || req.address.isBlank())
            return ResponseEntity.badRequest().body(ErrorResponse(400, "Bad Request", "name and address required"))
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(restaurantService.create(Restaurant(name = req.name, address = req.address)))
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody req: RestaurantCreateRequest): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok(restaurantService.update(id, Restaurant(name = req.name, address = req.address)))
        } catch (e: RuntimeException) {
            ResponseEntity.status(404).body(ErrorResponse(404, "Not Found", "Restaurant with id=$id not found"))
        }
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Any> =
        if (restaurantService.delete(id)) ResponseEntity.noContent().build()
        else ResponseEntity.status(404).body(ErrorResponse(404, "Not Found", "Restaurant with id=$id not found"))

    @GetMapping("/{id}/dishes")
    fun getDishes(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok(restaurantService.findDishes(id))
        } catch (e: RuntimeException) {
            ResponseEntity.status(404).body(ErrorResponse(404, "Not Found", "Restaurant with id=$id not found"))
        }
    }

    @PostMapping("/{restaurantId}/dishes")
    fun addDish(@PathVariable restaurantId: Long, @RequestBody req: DishCreateRequest): ResponseEntity<Any> {
        return try {
            val dish = restaurantService.addDish(
                restaurantId,
                Dish(name = req.name, description = req.description, price = req.price, isAvailable = req.isAvailable)
            )
            ResponseEntity.status(HttpStatus.CREATED).body(dish)
        } catch (e: RuntimeException) {
            ResponseEntity.status(404).body(ErrorResponse(404, "Not Found", "Restaurant with id=$restaurantId not found"))
        }
    }
}