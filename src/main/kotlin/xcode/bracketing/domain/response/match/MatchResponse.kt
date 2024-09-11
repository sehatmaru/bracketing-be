package xcode.bracketing.domain.response.match

import com.fasterxml.jackson.annotation.JsonFormat
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
    var groupId: Int? = null
    var number: Int? = null

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "UTC")
    var endAt: Date? = null
}

class TeamMatchResponse(var id: Int, var name: String, var number: Number, var score: Number, var isWinner: Boolean)
