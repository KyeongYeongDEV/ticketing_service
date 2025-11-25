package com.example.ticketing_service.user.presentation

import com.example.ticketing_service.user.application.UserService
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
    fun signup(@RequestBody request : SignupRequest) : ResponseEntity<Long> {
        //TODO: service layer에 Dto 생성하기
        val user = userService.signup(request.name, request.email, request.password);
        return ResponseEntity.ok(user)
    }

    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId : Long) : ResponseEntity<Any> {
        val user = userService.getUser(userId)
        return ResponseEntity.ok(user)
    }
}