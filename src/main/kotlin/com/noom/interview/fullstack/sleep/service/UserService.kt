package com.noom.interview.fullstack.sleep.service

import com.noom.interview.fullstack.sleep.db.UserRepository
import com.noom.interview.fullstack.sleep.model.User
import com.noom.interview.fullstack.sleep.model.dto.CreateUserDTO
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

@Service
class UserService(
    private val userRepository: UserRepository
) {
    
    @Transactional
    fun createUser(createUserDTO: CreateUserDTO): User {
        userRepository.findByEmail(createUserDTO.email)?.let {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Email already in use")
        }
        
        val user = User(
            name = createUserDTO.name,
            age = createUserDTO.age,
            email = createUserDTO.email
        )
        
        return userRepository.save(user)
    }
    
    @Transactional(readOnly = true)
    fun findById(id: UUID): User {
        return userRepository.findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: $id")
    }
}