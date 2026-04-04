package com.example.lab3.service

import com.example.lab3.application.service.UserService
import com.example.lab3.domain.model.User
import com.example.lab3.domain.port.UserRepositoryPort
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
class UserServiceTest {

    @MockK
    lateinit var userRepository: UserRepositoryPort

    @InjectMockKs
    lateinit var userService: UserService

    @Test
    fun `findAll возвращает список всех пользователей`() {
        val users = listOf(
            User(1L, "alice@example.com", "Alice", "Smith"),
            User(2L, "bob@example.com", "Bob", "Jones")
        )
        every { userRepository.findAll() } returns users

        val result = userService.findAll()

        assertEquals(2, result.size)
        assertEquals("alice@example.com", result[0].email)
    }

    @Test
    fun `findById возвращает пользователя если он существует`() {
        val user = User(1L, "alice@example.com", "Alice", "Smith")
        every { userRepository.findById(1L) } returns user

        val result = userService.findById(1L)

        assertEquals(1L, result.id)
        assertEquals("alice@example.com", result.email)
    }

    @Test
    fun `findById бросает NotFoundException для несуществующего id`() {
        every { userRepository.findById(999L) } returns null

        assertThrows<NotFoundException> {
            userService.findById(999L)
        }
    }

    @Test
    fun `createOrFind создаёт нового пользователя если email не занят`() {
        val user = User(0L, "new@example.com", "New", "User")
        val created = user.copy(id = 1L)
        every { userRepository.findByEmail("new@example.com") } returns null
        every { userRepository.create(user) } returns created

        val (result, isNew) = userService.createOrFind(user)

        assertTrue(isNew)
        assertEquals(1L, result.id)
        verify { userRepository.create(user) }
    }

    @Test
    fun `createOrFind возвращает существующего пользователя если email занят`() {
        val existing = User(1L, "alice@example.com", "Alice", "Smith")
        val user = User(0L, "alice@example.com", "Alice2", "Smith2")
        every { userRepository.findByEmail("alice@example.com") } returns existing

        val (result, isNew) = userService.createOrFind(user)

        assertFalse(isNew)
        assertEquals(1L, result.id)
        verify(exactly = 0) { userRepository.create(any()) }
    }

    @Test
    fun `update обновляет пользователя если он существует`() {
        val existing = User(1L, "alice@example.com", "Alice", "Smith")
        val updated = User(1L, "alice@example.com", "Alicia", "Smith")
        every { userRepository.findById(1L) } returns existing
        every { userRepository.update(updated) } returns updated

        val result = userService.update(1L, updated)

        assertEquals("Alicia", result.firstName)
    }

    @Test
    fun `update бросает NotFoundException если пользователь не найден`() {
        every { userRepository.findById(999L) } returns null

        assertThrows<NotFoundException> {
            userService.update(999L, User(999L, "x@x.com", "X", "X"))
        }
    }

    @Test
    fun `delete удаляет пользователя если он существует`() {
        every { userRepository.findById(1L) } returns User(1L, "alice@example.com", "Alice", "Smith")
        every { userRepository.delete(1L) } returns true

        userService.delete(1L)

        verify { userRepository.delete(1L) }
    }

    @Test
    fun `delete бросает NotFoundException если пользователь не найден`() {
        every { userRepository.findById(999L) } returns null

        assertThrows<NotFoundException> {
            userService.delete(999L)
        }
    }
}
