package com.example.lab3.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
class RestaurantIntegrationTest {

    companion object {
        @Container
        @ServiceConnection
        @JvmStatic
        val postgres = PostgreSQLContainer<Nothing>("postgres:17-alpine").apply {
            withDatabaseName("testdb")
            withUsername("test")
            withPassword("test")
        }

        val mapper = ObjectMapper()
    }

    @Autowired
    lateinit var context: WebApplicationContext

    private val mockMvc: MockMvc by lazy {
        MockMvcBuilders.webAppContextSetup(context).build()
    }

    @Test
    fun `GET all restaurants возвращает 200 и массив`() {
        mockMvc.get("/api/v1/restaurants")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$") { isArray() }
            }
    }

    @Test
    fun `POST restaurant возвращает 201 и созданный объект`() {
        mockMvc.post("/api/v1/restaurants") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"name": "Integration Test Place", "address": "ул. Тестовая, 1"}"""
        }.andExpect {
            status { isCreated() }
            jsonPath("$.id") { exists() }
            jsonPath("$.name") { value("Integration Test Place") }
            jsonPath("$.address") { value("ул. Тестовая, 1") }
        }
    }

    @Test
    fun `POST restaurant с пустым именем возвращает 400`() {
        mockMvc.post("/api/v1/restaurants") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"name": "", "address": "ул. Тестовая, 1"}"""
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.status") { value(400) }
        }
    }

    @Test
    fun `POST restaurant без обязательных полей возвращает 400`() {
        mockMvc.post("/api/v1/restaurants") {
            contentType = MediaType.APPLICATION_JSON
            content = """{}"""
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.status") { value(400) }
        }
    }

    @Test
    fun `GET несуществующий ресторан возвращает 404`() {
        mockMvc.get("/api/v1/restaurants/999999")
            .andExpect {
                status { isNotFound() }
                jsonPath("$.status") { value(404) }
            }
    }

    @Test
    fun `POST дубликат ресторана возвращает 409`() {
        val name = "Duplicate Restaurant ${System.currentTimeMillis()}"
        val body = """{"name": "$name", "address": "ул. Тестовая, 1"}"""

        mockMvc.post("/api/v1/restaurants") {
            contentType = MediaType.APPLICATION_JSON
            content = body
        }.andExpect { status { isCreated() } }

        mockMvc.post("/api/v1/restaurants") {
            contentType = MediaType.APPLICATION_JSON
            content = body
        }.andExpect {
            status { isConflict() }
            jsonPath("$.status") { value(409) }
        }
    }

    @Test
    fun `PUT обновляет ресторан и возвращает 200`() {
        val created = mockMvc.post("/api/v1/restaurants") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"name": "To Update ${System.currentTimeMillis()}", "address": "Old Address"}"""
        }.andExpect { status { isCreated() } }
            .andReturn()

        val id = mapper.readTree(created.response.contentAsString)["id"].asLong()

        mockMvc.put("/api/v1/restaurants/$id") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"name": "Updated Name", "address": "New Address"}"""
        }.andExpect {
            status { isOk() }
            jsonPath("$.address") { value("New Address") }
        }
    }

    @Test
    fun `DELETE удаляет ресторан и возвращает 204`() {
        val created = mockMvc.post("/api/v1/restaurants") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"name": "To Delete ${System.currentTimeMillis()}", "address": "Address"}"""
        }.andExpect { status { isCreated() } }
            .andReturn()

        val id = mapper.readTree(created.response.contentAsString)["id"].asLong()

        mockMvc.delete("/api/v1/restaurants/$id")
            .andExpect { status { isNoContent() } }
    }

    @Test
    fun `DELETE несуществующий ресторан возвращает 404`() {
        mockMvc.delete("/api/v1/restaurants/999999")
            .andExpect {
                status { isNotFound() }
                jsonPath("$.status") { value(404) }
            }
    }

    @Test
    fun `GET dishes ресторана возвращает 200 и массив`() {
        val created = mockMvc.post("/api/v1/restaurants") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"name": "Dishes Test ${System.currentTimeMillis()}", "address": "Address"}"""
        }.andExpect { status { isCreated() } }
            .andReturn()

        val id = mapper.readTree(created.response.contentAsString)["id"].asLong()

        mockMvc.get("/api/v1/restaurants/$id/dishes")
            .andExpect {
                status { isOk() }
                jsonPath("$") { isArray() }
            }
    }
}
