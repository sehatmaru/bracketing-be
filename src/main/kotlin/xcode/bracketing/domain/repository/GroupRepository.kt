package xcode.bracketing.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import xcode.bracketing.domain.model.Group

@Repository
interface GroupRepository : JpaRepository<Group?, String?> {
    fun findByTournamentId(tournamentId: Int): List<Group>?
}