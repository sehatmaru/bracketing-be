package xcode.bracketing.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import xcode.bracketing.domain.enums.MatchStage
import xcode.bracketing.domain.enums.MatchStatus
import xcode.bracketing.domain.model.Match

@Repository
interface MatchRepository : JpaRepository<Match?, String?> {

    fun findByGroupId(groupId: Int): List<Match>?

    fun findByTournamentId(tournamentId: Int): List<Match>?

    fun findByNextMatchId(nextMatchId: Int): List<Match>?

    fun findByStageAndTournamentIdAndStatus(stage: MatchStage, tournamentId: Int, status: MatchStatus): List<Match>?

    fun findByGroupIdAndStatus(groupId: Int, status: MatchStatus): List<Match>?

    fun findByTournamentIdAndStage(tournamentId: Int, stage: MatchStage): List<Match>?

    fun findByTournamentIdAndStageIsNot(tournamentId: Int, stage: MatchStage): List<Match>?
}