package com.noom.interview.fullstack.sleep.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.noom.interview.fullstack.sleep.model.User
import com.noom.interview.fullstack.sleep.model.dto.CreateUserDTO
import com.noom.interview.fullstack.sleep.service.UserService
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*

@WebMvcTest(UserController::class)
class UserControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var userService: UserService

    @Test
    fun `createUser should return CREATED status and the created user`() {
        // Arrange
        val userId = UUID.randomUUID()
        val createUserDTO = CreateUserDTO("John", 21, "john.doe@example.com")
        val createdUser = User(
            id = userId,
            name = "John",
            age = 21,
            email = "john.doe@example.com"
        )

        whenever(userService.createUser(any())).thenReturn(createdUser)

        // Act & Assert
        mockMvc.perform(
            post("/api/users")
                .content(objectMapper.writeValueAsString(createUserDTO))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", equalTo(userId.toString())))
            .andExpect(jsonPath("$.name", equalTo("John")))
            .andExpect(jsonPath("$.age", equalTo(21)))
            .andExpect(jsonPath("$.email", equalTo("john.doe@example.com")))
    }

    @Test
    fun `getUserById should return OK status and the requested user`() {
        // Arrange
        val userId = UUID.randomUUID()
        val user = User(
            id = userId,
            name = "Jane",
            age = 30,
            email = "jane.doe@example.com"
        )

        whenever(userService.findById(userId)).thenReturn(user)

        // Act & Assert
        mockMvc.perform(
            get("/api/users/$userId")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", equalTo(userId.toString())))
            .andExpect(jsonPath("$.name", equalTo("Jane")))
            .andExpect(jsonPath("$.age", equalTo(30)))
            .andExpect(jsonPath("$.email", equalTo("jane.doe@example.com")))
    }
}