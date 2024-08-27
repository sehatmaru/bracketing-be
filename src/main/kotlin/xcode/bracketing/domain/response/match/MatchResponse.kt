package xcode.bracketing.domain.response.match

import xcode.bracketing.domain.enums.MatchStage
import xcode.bracketing.domain.enums.MatchStatus
import java.util.*

class MatchResponse {
    var id = 0
    var stage: MatchStage? = null
    var teamAId = 0
    var teamAScore = 0
    var teamBId = 0
    var teamBScore = 0
    var winner: Int? = null
    var status: MatchStatus = MatchStatus.WAITING
    var endAt: Date? = null
}