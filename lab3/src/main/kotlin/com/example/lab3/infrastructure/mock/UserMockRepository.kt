package com.example.lab3.infrastructure.mock

import com.example.lab3.domain.model.User
import com.example.lab3.domain.port.UserRepositoryPort
import org.springframework.context.annotation.Conditional
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

@Repository
@Profile("mock")
class UserMockRepository : UserRepositoryPort {

    private val storage = ConcurrentHashMap<Long, User>()
    private val seq = AtomicLong(1)

    override fun findAll(): List<User> = storage.values.toList()

    override fun findById(id: Long): User? = storage[id]

    override fun findByEmail(email: String): User? =
        storage.values.find { it.email == email }

    override fun create(user: User): User {
        val id = seq.getAndIncrement()
        val saved = user.copy(id = id)
        storage[id] = saved
        return saved
    }

    override fun update(user: User): User {
        storage[user.id] = user
        return user
    }

    override fun delete(id: Long): Boolean =
        storage.remove(id) != null
}