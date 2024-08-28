package xcode.bracketing.domain.model

import jakarta.persistence.*
import org.hibernate.annotations.DynamicUpdate
import xcode.bracketing.domain.enums.MatchStage
import xcode.bracketing.domain.enums.MatchStatus
import java.util.*

@Entity
@Table(name = "t_match")
@DynamicUpdate
class Match {

    @Id
    @Column(name = "id", length = 36)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id = 0

    @Column(name = "tournament_id")
    var tournamentId = 0

    @Column(name = "group_id")
    var groupId: Int? = null

    @Column(name = "stage")
    var stage: MatchStage? = null

    @Column(name = "team_a_number")
    var teamANumber = 0

    @Column(name = "team_a_score")
    var teamAScore = 0

    @Column(name = "team_b_number")
    var teamBNumber = 0

    @Column(name = "team_b_score")
    var teamBScore = 0

    @Column(name = "winner")
    var winner: Int? = null

    @Column(name = "next_match_id")
    var nextMatchId: Int = 0

    @Column(name = "status")
    var status: MatchStatus = MatchStatus.WAITING

    @Column(name = "end_at")
    var endAt: Date? = null

    fun isStarted(): Boolean {
        return status != MatchStatus.WAITING
    }
}