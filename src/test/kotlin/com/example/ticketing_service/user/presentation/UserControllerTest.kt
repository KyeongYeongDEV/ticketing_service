package com.example.ticketing_service.user.presentation

import com.example.ticketing_service.user.application.UserService
import com.example.ticketing_service.user.application.dto.UserResponse
import com.example.ticketing_service.user.presentation.dto.SignupRequest

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(UserController::class)
class UserControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockkBean
    lateinit var userService: UserService

    @MockkBean(relaxed = true)
    lateinit var jpaMetamodelMappingContext: JpaMetamodelMappingContext

    @Test
    @DisplayName("회원가입 API 성공 시 201 Created와 Location 헤더를 반환한다")
    fun signup_api_success() {
        // Given
        val request = SignupRequest("신짱구", "test@email.com", "pw")
        every { userService.signup(any()) } returns 1L

        // When & Then
        mockMvc.perform(
            post("/api/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(header().string("Location", "/api/users/1"))
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data").value(1))
    }

    @Test
    @DisplayName("유저 조회 API 성공 시 200 OK와 UserResponse를 반환한다")
    fun get_user_api_success() {
        val userId = 1L
        val response = UserResponse(userId, "신짱구", "test@email.com")
        every { userService.getUser(userId) } returns response

        mockMvc.perform(get("/api/users/$userId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.email").value("test@email.com"))
            .andExpect(jsonPath("$.data.name").value("신짱구"))
    }
}