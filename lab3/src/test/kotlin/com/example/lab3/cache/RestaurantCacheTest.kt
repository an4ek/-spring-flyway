package com.example.lab3.cache

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.CacheManager
import org.springframework.cache.interceptor.SimpleKey
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(EmbeddedRedisConfig::class)
@TestPropertySource(properties = [
    "spring.data.redis.host=localhost",
    "spring.data.redis.port=6379"
])
class RestaurantCacheTest {

    @Autowired
    lateinit var context: WebApplicationContext

    @Autowired
    lateinit var cacheManager: CacheManager

    private val mockMvc: MockMvc by lazy {
        MockMvcBuilders.webAppContextSetup(context).build()
    }

    private val mapper = ObjectMapper()

    @BeforeEach
    fun clearCache() {
        cacheManager.cacheNames.forEach { cacheManager.getCache(it)?.clear() }
    }

    @Test
    fun `повторный GET restaurants не идёт в БД - кэш-хит`() {
        mockMvc.get("/api/v1/restaurants").andExpect { status { isOk() } }
        mockMvc.get("/api/v1/restaurants").andExpect { status { isOk() } }
        val cache = cacheManager.getCache("restaurants")
        assertNotNull(cache?.get(SimpleKey.EMPTY))
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `создание ресторана инвалидирует кэш`() {
        mockMvc.get("/api/v1/restaurants").andExpect { status { isOk() } }
        assertNotNull(cacheManager.getCache("restaurants")?.get(SimpleKey.EMPTY))

        mockMvc.post("/api/v1/restaurants") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"name": "Cache Test ${System.currentTimeMillis()}", "address": "ул. Теста, 1"}"""
        }.andExpect { status { isCreated() } }

        assertNull(cacheManager.getCache("restaurants")?.get(SimpleKey.EMPTY))
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `удаление ресторана инвалидирует кэш`() {
        val created = mockMvc.post("/api/v1/restaurants") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"name": "To Delete Cache ${System.currentTimeMillis()}", "address": "Address"}"""
        }.andExpect { status { isCreated() } }
            .andReturn()

        val id = mapper.readTree(created.response.contentAsString)["id"].asLong()

        mockMvc.get("/api/v1/restaurants").andExpect { status { isOk() } }

        mockMvc.delete("/api/v1/restaurants/$id").andExpect { status { isNoContent() } }

        assertNull(cacheManager.getCache("restaurants")?.get(SimpleKey.EMPTY))
    }
}
