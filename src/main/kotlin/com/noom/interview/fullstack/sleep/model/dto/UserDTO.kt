package com.noom.interview.fullstack.sleep.model.dto

import com.noom.interview.fullstack.sleep.model.User
import java.util.UUID


data class UserDTO(
    val id: UUID?,
    val name: String,
    val age: Int,
    val email: String
) {
    constructor(user: User) : this(
        id = user.id,
        name = user.name,
        age = user.age,
        email = user.email
    )
}