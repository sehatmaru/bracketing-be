package xcode.bracketing.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import xcode.bracketing.domain.model.Team

@Repository
interface TeamRepository : JpaRepository<Team?, String?> {
    fun findByGroupId(groupId: Int): List<Team?>?

    fun findByTournamentId(tournamentId: Int): List<Team?>?

    fun findByNumber(number: Int): Team?
}