package com.example.ticketing_service.user.presentation


import com.example.ticketing_service.global.common.response.ApiResponse
import com.example.ticketing_service.user.application.UserService
import com.example.ticketing_service.user.application.dto.UserResponse
import com.example.ticketing_service.user.presentation.dto.SignupRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/users")
class UserController (
    private val userService: UserService
){
    @PostMapping("/signup")
    fun signup(@RequestBody request : SignupRequest) : ResponseEntity<ApiResponse<Long>> {
        val command = request.toCommand()
        val userId = userService.signup(command)

        val uri = java.net.URI.create("/api/users/$userId")

        return ResponseEntity
            .created(uri)
            .body(ApiResponse.success(userId, "회원가입이 완료되었습니다."))

    }

    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId : Long) : ResponseEntity<ApiResponse<UserResponse>> {
        val userResponse = userService.getUser(userId)

        return ResponseEntity.ok(
            ApiResponse.success(userResponse)
        )
    }
}