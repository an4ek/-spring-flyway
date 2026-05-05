package com.example.lab3.web.controller

import com.example.lab3.application.service.DishService
import com.example.lab3.application.service.RestaurantService
import com.example.lab3.domain.model.Dish
import com.example.lab3.domain.model.Restaurant
import com.example.lab3.web.dto.DishCreateRequest
import com.example.lab3.web.dto.RestaurantCreateRequest
import com.example.lab3.web.dto.RestaurantUpdateRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/restaurants")
@Tag(name = "Restaurants", description = "Управление ресторанами")
class RestaurantController(
    private val restaurantService: RestaurantService,
    private val dishService: DishService
) {

    @GetMapping
    @Operation(summary = "Получить список всех ресторанов")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Список ресторанов")
    ])
    fun getAll(): ResponseEntity<List<Restaurant>> =
        ResponseEntity.ok(restaurantService.findAll())

    @GetMapping("/{id}")
    @Operation(summary = "Получить ресторан по ID")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Ресторан найден"),
        ApiResponse(responseCode = "404", description = "Ресторан не найден")
    ])
    fun getById(@PathVariable id: Long): ResponseEntity<Restaurant> =
        ResponseEntity.ok(restaurantService.findById(id))

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Создать ресторан (только ADMIN)")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Ресторан создан"),
        ApiResponse(responseCode = "400", description = "Невалидные данные"),
        ApiResponse(responseCode = "401", description = "Не аутентифицирован"),
        ApiResponse(responseCode = "403", description = "Нет прав"),
        ApiResponse(responseCode = "409", description = "Ресторан с таким именем уже существует")
    ])
    fun create(@Valid @RequestBody request: RestaurantCreateRequest): ResponseEntity<Restaurant> {
        val restaurant = restaurantService.create(
            Restaurant(0, request.name.orEmpty(), request.address.orEmpty())
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurant)
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Обновить ресторан (только ADMIN)")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: RestaurantUpdateRequest
    ): ResponseEntity<Restaurant> =
        ResponseEntity.ok(
            restaurantService.update(id, Restaurant(id, request.name.orEmpty(), request.address.orEmpty()))
        )

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить ресторан (только ADMIN)")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        restaurantService.delete(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{id}/dishes")
    @Operation(summary = "Получить меню ресторана")
    fun getDishes(@PathVariable id: Long): ResponseEntity<List<Dish>> {
        restaurantService.findById(id)
        return ResponseEntity.ok(dishService.findAll().filter { it.restaurantId == id })
    }

    @PostMapping("/{id}/dishes")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Добавить блюдо в ресторан (только ADMIN)")
    fun addDish(
        @PathVariable id: Long,
        @Valid @RequestBody request: DishCreateRequest
    ): ResponseEntity<Dish> {
        restaurantService.findById(id)
        val dish = Dish(0, request.name.orEmpty(), request.description.orEmpty(), request.price ?: 0.0, request.isAvailable ?: true, id)
        val (created, _) = dishService.createOrFind(dish)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }
}
