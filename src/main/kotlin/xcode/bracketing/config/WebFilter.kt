package xcode.bracketing.config

import io.jsonwebtoken.Jwts
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import org.springframework.web.filter.GenericFilterBean
import xcode.bracketing.exception.AppException
import xcode.bracketing.shared.ResponseCode

@Service
class WebFilter : GenericFilterBean() {

    override fun doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, filterChain: FilterChain) {
        try {
            val request = servletRequest as HttpServletRequest
            val response = servletResponse as HttpServletResponse
            val uri = request.requestURI

            if (uri.startsWith("/api")) {
                val authHeader = request.getHeader("authorization")

                if ("OPTIONS" == request.method) {
                    response.status = HttpServletResponse.SC_OK
                    filterChain.doFilter(request, response)
                } else {
                    if (!authHeader.startsWith("Bearer ")) {
                        throw ServletException()
                    }
                }
                val token = authHeader.substring(7)

                Jwts.parser().setSigningKey("xcode").parseClaimsJws(token).body
            }

            filterChain.doFilter(request, response)
        } catch (ex: Exception) {
            throw AppException(ResponseCode.TOKEN_ERROR_MESSAGE)
        }
    }

    @Bean
    fun corsFilter(): FilterRegistrationBean<CorsFilter> {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowCredentials = true
        config.addAllowedOrigin("*")
        config.addAllowedHeader("*")
        config.addAllowedMethod("*")
        source.registerCorsConfiguration("/**", config)
        val bean = FilterRegistrationBean(CorsFilter(source))
        bean.order = 0

        return bean
    }
}