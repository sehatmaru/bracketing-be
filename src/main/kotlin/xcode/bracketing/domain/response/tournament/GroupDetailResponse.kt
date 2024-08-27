package xcode.bracketing.domain.response.tournament

import xcode.bracketing.domain.enums.GroupStatus
import java.util.*

class GroupDetailResponse {
    var id: Int? = null
    var tournamentId = 0
    var teams: MutableList<TeamGroupResponse> = mutableListOf()
    var status: GroupStatus = GroupStatus.WAITING
    var startedAt: Date? = null
    var endedAt: Date? = null
}

class TeamGroupResponse {
    var id: Int? = null
    var name: String = ""
    var score: Int = 0
    var played: Int = 0
    var wins: Int = 0
    var loses: Int = 0
    var draws: Int = 0
    var point: Int = 0
}