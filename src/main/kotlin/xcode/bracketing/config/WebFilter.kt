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
import java.io.IOException

@Service
class WebFilter : GenericFilterBean() {

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, filterChain: FilterChain) {
        val request: HttpServletRequest = servletRequest as HttpServletRequest
        val response: HttpServletResponse = servletResponse as HttpServletResponse
        val authHeader: String = request.getHeader("authorization")
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
        filterChain.doFilter(request, response)
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