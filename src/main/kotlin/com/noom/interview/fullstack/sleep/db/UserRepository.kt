package com.noom.interview.fullstack.sleep.db

import com.noom.interview.fullstack.sleep.model.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserRepository : JpaRepository<User, UUID> {
    fun findByEmail(email: String): User?
}