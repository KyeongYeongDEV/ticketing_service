package com.example.ticketing_service.user.presentation.dto

import com.example.ticketing_service.user.application.dto.SignupCommand
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class SignupRequest(
    @field:NotBlank(message = "이름은 필수입니다.")
    val name : String,

    @field:NotBlank(message = "이메일은 필수입니다.")
    @field:Email(message = "이메일 형식이 올바르지 않습니다")
    val email : String,

    @field:NotBlank(message = "비밀번호는 필수입니다.")
    @field:Size(min = 6, message = "비밀번호는 6자 이상이여야 합니다..")
    val password : String

) {
    fun toCommand() : SignupCommand {
        return SignupCommand(
            name = this.name,
            email = this.email,
            password = this.password
        )
    }
}
