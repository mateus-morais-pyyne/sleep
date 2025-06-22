package com.noom.interview.fullstack.sleep.controller

import com.noom.interview.fullstack.sleep.model.dto.CreateUserDTO
import com.noom.interview.fullstack.sleep.model.dto.UserDTO
import com.noom.interview.fullstack.sleep.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    fun createUser(@RequestBody createUserDTO: CreateUserDTO): UserDTO {
        val createdUser = userService.createUser(createUserDTO)
        return UserDTO(createdUser)
    }
    
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    fun getUserById(@PathVariable id: UUID): UserDTO{
        val user = userService.findById(id)
        return UserDTO(user)
    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    fun getUserById(): List<UserDTO>{
        val user = userService.list()
        return user.map{user -> UserDTO(user)}}
}