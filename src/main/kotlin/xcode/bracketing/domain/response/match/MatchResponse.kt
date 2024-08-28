package xcode.bracketing.domain.response.match

import xcode.bracketing.domain.enums.MatchStage
import xcode.bracketing.domain.enums.MatchStatus
import java.util.*

class MatchResponse {
    var id = 0
    var stage: MatchStage? = null
    var teamA: TeamMatchResponse? = null
    var teamB: TeamMatchResponse? = null
    var winner: Int? = null
    var status: MatchStatus = MatchStatus.WAITING
    var endAt: Date? = null
}

class TeamMatchResponse(var id: Int, var name: String, var number: Number)
