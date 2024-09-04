package xcode.bracketing.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import xcode.bracketing.interceptor.UserInterceptor

@Configuration
class WebConfig(
    private val userInterceptor: UserInterceptor
) : WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:4200")
            .allowedMethods("*")
            .allowedHeaders("*")
            .exposedHeaders("Authorization")
            .allowCredentials(true)
            .maxAge(3600)
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(userInterceptor).excludePathPatterns(
            "/auth/login",
            "/auth/register"
        )
    }
}
