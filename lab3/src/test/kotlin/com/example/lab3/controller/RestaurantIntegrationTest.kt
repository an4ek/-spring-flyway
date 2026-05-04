package com.example.lab3.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import com.example.lab3.cache.EmbeddedRedisConfig

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(EmbeddedRedisConfig::class)
class RestaurantIntegrationTest {

    companion object {
        val mapper = ObjectMapper()
    }

    @Autowired
    lateinit var context: WebApplicationContext

    private val mockMvc: MockMvc by lazy {
        val builder = MockMvcBuilders.webAppContextSetup(context)
        builder.apply<org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder>(SecurityMockMvcConfigurers.springSecurity())
        builder.build()
    }

    @Test
    fun `GET all restaurants возвращает 200 и массив без токена`() {
        mockMvc.get("/api/v1/restaurants")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$") { isArray() }
            }
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `POST restaurant возвращает 201 и созданный объект`() {
        mockMvc.post("/api/v1/restaurants") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"name": "Integration Test Place ${System.currentTimeMillis()}", "address": "ул. Тестовая, 1"}"""
        }.andExpect {
            status { isCreated() }
            jsonPath("$.id") { exists() }
            jsonPath("$.name") { exists() }
            jsonPath("$.address") { value("ул. Тестовая, 1") }
        }
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
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
    @WithMockUser(roles = ["ADMIN"])
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
    @WithMockUser(roles = ["ADMIN"])
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
    @WithMockUser(roles = ["ADMIN"])
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
    @WithMockUser(roles = ["ADMIN"])
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
    @WithMockUser(roles = ["ADMIN"])
    fun `DELETE несуществующий ресторан возвращает 404`() {
        mockMvc.delete("/api/v1/restaurants/999999")
            .andExpect {
                status { isNotFound() }
                jsonPath("$.status") { value(404) }
            }
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
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

    @Test
    fun `POST restaurant без токена возвращает 401`() {
        mockMvc.post("/api/v1/restaurants") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"name": "Test", "address": "Address"}"""
        }.andExpect {
            status { isUnauthorized() }
        }
    }

    @Test
    @WithMockUser(roles = ["USER"])
    fun `POST restaurant с ролью USER возвращает 403`() {
        mockMvc.post("/api/v1/restaurants") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"name": "Test", "address": "Address"}"""
        }.andExpect {
            status { isForbidden() }
        }
    }

    @Test
    @WithMockUser(roles = ["USER"])
    fun `DELETE restaurant с ролью USER возвращает 403`() {
        mockMvc.delete("/api/v1/restaurants/1")
            .andExpect {
                status { isForbidden() }
            }
    }
}
