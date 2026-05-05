package com.example.lab3.service

import com.example.lab3.application.service.RestaurantService
import com.example.lab3.domain.model.Dish
import com.example.lab3.domain.model.Restaurant
import com.example.lab3.domain.port.RestaurantRepositoryPort
import com.example.lab3.web.exception.AlreadyExistsException
import com.example.lab3.web.exception.NotFoundException
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class RestaurantServiceTest {

    @MockK
    lateinit var restaurantRepository: RestaurantRepositoryPort

    @InjectMockKs
    lateinit var restaurantService: RestaurantService

    @Test
    fun `findAll возвращает список всех ресторанов`() {
        val restaurants = listOf(
            Restaurant(1L, "Pizza Place", "ул. Ленина, 1"),
            Restaurant(2L, "Sushi Bar", "ул. Мира, 2")
        )
        every { restaurantRepository.findAll() } returns restaurants

        val result = restaurantService.findAll()

        assertEquals(2, result.size)
        assertEquals("Pizza Place", result[0].name)
    }

    @Test
    fun `findById возвращает ресторан если он существует`() {
        val restaurant = Restaurant(1L, "Pizza Place", "ул. Ленина, 1")
        every { restaurantRepository.findById(1L) } returns restaurant

        val result = restaurantService.findById(1L)

        assertEquals(1L, result.id)
        assertEquals("Pizza Place", result.name)
    }

    @Test
    fun `findById бросает NotFoundException для несуществующего id`() {
        every { restaurantRepository.findById(999L) } returns null

        assertThrows<NotFoundException> {
            restaurantService.findById(999L)
        }
    }

    @Test
    fun `create создаёт ресторан если имя уникально`() {
        val restaurant = Restaurant(0L, "New Place", "ул. Тестовая, 1")
        val created = restaurant.copy(id = 1L)
        every { restaurantRepository.existsByName("New Place") } returns false
        every { restaurantRepository.create(restaurant) } returns created

        val result = restaurantService.create(restaurant)

        assertEquals(1L, result.id)
        assertEquals("New Place", result.name)
        verify { restaurantRepository.create(restaurant) }
    }

    @Test
    fun `create бросает AlreadyExistsException при дублировании имени`() {
        val restaurant = Restaurant(0L, "Pizza Place", "ул. Ленина, 1")
        every { restaurantRepository.existsByName("Pizza Place") } returns true

        assertThrows<AlreadyExistsException> {
            restaurantService.create(restaurant)
        }

        verify(exactly = 0) { restaurantRepository.create(any()) }
    }

    @Test
    fun `update обновляет ресторан если он существует`() {
        val existing = Restaurant(1L, "Old Name", "Old Address")
        val updated = Restaurant(1L, "New Name", "New Address")
        every { restaurantRepository.findById(1L) } returns existing
        every { restaurantRepository.update(updated) } returns updated

        val result = restaurantService.update(1L, updated)

        assertEquals("New Name", result.name)
        assertEquals("New Address", result.address)
    }

    @Test
    fun `update бросает NotFoundException если ресторан не найден`() {
        every { restaurantRepository.findById(999L) } returns null

        assertThrows<NotFoundException> {
            restaurantService.update(999L, Restaurant(999L, "Name", "Address"))
        }
    }

    @Test
    fun `delete удаляет ресторан если он существует`() {
        every { restaurantRepository.findById(1L) } returns Restaurant(1L, "Pizza Place", "ул. Ленина, 1")
        every { restaurantRepository.delete(1L) } returns true

        restaurantService.delete(1L)

        verify { restaurantRepository.delete(1L) }
    }

    @Test
    fun `delete бросает NotFoundException если ресторан не найден`() {
        every { restaurantRepository.findById(999L) } returns null

        assertThrows<NotFoundException> {
            restaurantService.delete(999L)
        }
    }

    @Test
    fun `getDishes возвращает блюда ресторана`() {
        val dishes = listOf(
            Dish(1L, "Пицца", "Вкусная", 500.0, true, 1L),
            Dish(2L, "Паста", "Нежная", 400.0, true, 1L)
        )
        every { restaurantRepository.findById(1L) } returns Restaurant(1L, "Pizza Place", "ул. Ленина, 1")
        every { restaurantRepository.findDishes(1L) } returns dishes

        val result = restaurantService.getDishes(1L)

        assertEquals(2, result.size)
        assertEquals("Пицца", result[0].name)
    }

    @Test
    fun `getDishes бросает NotFoundException если ресторан не найден`() {
        every { restaurantRepository.findById(999L) } returns null

        assertThrows<NotFoundException> {
            restaurantService.getDishes(999L)
        }
    }

    @Test
    fun `addDish добавляет блюдо в ресторан`() {
        val dish = Dish(0L, "Пицца", "Вкусная", 500.0, true, 1L)
        val created = dish.copy(id = 1L)
        every { restaurantRepository.findById(1L) } returns Restaurant(1L, "Pizza Place", "ул. Ленина, 1")
        every { restaurantRepository.addDish(1L, dish) } returns created

        val result = restaurantService.addDish(1L, dish)

        assertEquals(1L, result.id)
        assertEquals("Пицца", result.name)
    }
}
