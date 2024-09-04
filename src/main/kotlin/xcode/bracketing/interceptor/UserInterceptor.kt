package xcode.bracketing.interceptor

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import xcode.bracketing.domain.model.CurrentAuth
import xcode.bracketing.domain.repository.TokenRepository
import xcode.bracketing.exception.AppException
import xcode.bracketing.shared.ResponseCode

@Component
@EnableAsync
class UserInterceptor @Autowired constructor(
    private val tokenRepository: TokenRepository
) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val token = request.getHeader("Authorization") ?: request.getHeader("authorization")

        if (token != null) {
            val tokenModel = tokenRepository.findByToken(token.substring(7))

            if (tokenModel != null && !tokenModel.isValid()) {
                throw AppException(ResponseCode.TOKEN_ERROR_MESSAGE)
            }

            CurrentAuth.set(tokenModel!!)
        }

        return true
    }

    override fun afterCompletion(request: HttpServletRequest, response: HttpServletResponse, handler: Any, ex: Exception?) {
        CurrentAuth.remove()
    }
}