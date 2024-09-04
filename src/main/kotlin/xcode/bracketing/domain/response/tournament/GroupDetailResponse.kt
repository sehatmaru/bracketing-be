package xcode.bracketing.domain.response.tournament

import com.fasterxml.jackson.annotation.JsonFormat
import xcode.bracketing.domain.enums.GroupStatus
import java.util.*

class GroupDetailResponse {
    var id: Int? = null
    var tournamentId = 0
    var teams: MutableList<TeamGroupResponse> = mutableListOf()
    var status: GroupStatus = GroupStatus.WAITING

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "UTC")
    var startedAt: Date? = null

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "UTC")
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