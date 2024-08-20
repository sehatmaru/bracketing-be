package xcode.bracketing.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import xcode.bracketing.domain.model.Token
import java.util.*

@Repository
interface TokenRepository : JpaRepository<Token?, String?> {
    fun findByToken(secureId: String?): Optional<Token?>?
}