package xcode.bracketing.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import xcode.bracketing.domain.request.auth.LoginRequest
import xcode.bracketing.domain.request.auth.RegisterRequest
import xcode.bracketing.domain.response.BaseResponse
import xcode.bracketing.domain.response.auth.LoginResponse
import xcode.bracketing.domain.response.auth.RegisterResponse
import xcode.bracketing.service.AuthService

@RestController
@RequestMapping(value = ["auth"])
class AuthApi @Autowired constructor(
    private val authService: AuthService
) {

    @PostMapping("/login")
    fun login(@RequestBody @Validated request: LoginRequest): ResponseEntity<BaseResponse<LoginResponse>> {
        val response: BaseResponse<LoginResponse> = authService.login(request)

        return ResponseEntity
            .status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(response)
    }

    @PostMapping("/register")
    fun register(@RequestBody @Validated request: RegisterRequest): BaseResponse<RegisterResponse> {
        val response: BaseResponse<RegisterResponse> = authService.register(request)

        return response
    }

    @PostMapping("/logout")
    fun logout() {
        authService.logout()

        return BaseResponse<Boolean>().setSuccess(true)
    }
}