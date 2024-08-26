package xcode.bracketing.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import xcode.bracketing.domain.model.Match

@Repository
interface MatchRepository : JpaRepository<Match?, String?> {
//    fun findByUsernameAndDeletedAtIsNull(username: String): User?
}