package xcode.bracketing.domain.request.match

class MatchScoreRequest {
    var teamA: TeamMatchScoreRequest? = null
    var teamB: TeamMatchScoreRequest? = null
}

class TeamMatchScoreRequest {
    var score: Int = 0
    var isWinner = false
}