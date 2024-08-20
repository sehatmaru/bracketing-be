package xcode.bracketing

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@EnableEncryptableProperties
@SpringBootApplication
class BracketingBeApplication

fun main(args: Array<String>) {
    runApplication<BracketingBeApplication>(*args)
}
