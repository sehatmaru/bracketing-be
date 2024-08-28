package xcode.bracketing.domain.response.match

class MatchPlayedResponse {
    var winner: TeamMatchPlayedResponse? = null
}

class TeamMatchPlayedResponse(var id: Int, var name: String, var number: Number)
