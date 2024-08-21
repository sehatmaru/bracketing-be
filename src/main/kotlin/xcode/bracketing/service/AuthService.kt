package xcode.bracketing.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import xcode.bracketing.domain.model.CurrentAuth
import xcode.bracketing.domain.model.Token
import xcode.bracketing.domain.model.User
import xcode.bracketing.domain.repository.TokenRepository
import xcode.bracketing.domain.repository.UserRepository
import xcode.bracketing.domain.request.auth.LoginRequest
import xcode.bracketing.domain.request.auth.RegisterRequest
import xcode.bracketing.domain.response.BaseResponse
import xcode.bracketing.domain.response.auth.LoginResponse
import xcode.bracketing.domain.response.auth.RegisterResponse
import xcode.bracketing.exception.AppException
import xcode.bracketing.shared.ResponseCode
import xcode.bracketing.shared.ResponseCode.AUTH_ERROR_MESSAGE
import xcode.bracketing.shared.ResponseCode.EXIST_MESSAGE
import xcode.bracketing.shared.ResponseCode.PARAMS_ERROR_MESSAGE
import xcode.bracketing.utils.CommonUtil.encryptor
import xcode.bracketing.utils.CommonUtil.getTomorrowDate
import java.util.*

@Service
class AuthService @Autowired constructor(
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository
) {

    fun login(request: LoginRequest): BaseResponse<LoginResponse> {
        val baseResponse = BaseResponse<LoginResponse>()

        if (request.username.isEmpty() || request.password.isEmpty()) {
            throw AppException(PARAMS_ERROR_MESSAGE)
        }

        val user = userRepository.findByUsernameAndDeletedAtIsNull(request.username)

        if (user == null || request.password != encryptor(user.password, false)){
            throw AppException(AUTH_ERROR_MESSAGE)
        }

        val token = jwtService.generateToken(user)
        tokenRepository.save(Token(token, user.id, getTomorrowDate()))

        val response = LoginResponse()
        response.id = user.id
        response.username = user.username
        response.fullName = user.fullName
        response.email = user.email
        response.accessToken = token

        baseResponse.setSuccess(response)

        return baseResponse
    }

    fun register(request: RegisterRequest): BaseResponse<RegisterResponse> {
        val baseResponse = BaseResponse<RegisterResponse>()

        if (request.username.isEmpty() || request.password.isEmpty()
            || request.fullName.isEmpty() || request.email.isEmpty()
        ) {
            throw AppException(PARAMS_ERROR_MESSAGE)
        }

        if (userRepository.findByUsernameAndDeletedAtIsNull(request.username) != null) {
            throw AppException(EXIST_MESSAGE)
        }

        val user = User()
        user.username = request.username
        user.fullName = request.fullName
        user.email = request.email
        user.password = encryptor(request.password, true)
        user.createdAt = Date()

        userRepository.save(user)

        val response = RegisterResponse()
        response.id = user.id
        response.username = user.username
        response.fullName = user.fullName
        response.email = user.email

        baseResponse.setSuccess(response)

        return baseResponse
    }

    fun logout() {
        val token = tokenRepository.findByToken(CurrentAuth.get().token)

        if (token == null) {
            throw AppException(ResponseCode.NOT_FOUND_MESSAGE)
        } else {
            tokenRepository.delete(token)
        }
    }
}