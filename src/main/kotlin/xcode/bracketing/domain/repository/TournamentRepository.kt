package xcode.bracketing.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import xcode.bracketing.domain.model.Tournament

@Repository
interface TournamentRepository : JpaRepository<Tournament?, String?> {
//    fun findByUsernameAndDeletedAtIsNull(username: String): User?
}