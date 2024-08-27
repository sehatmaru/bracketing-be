package xcode.bracketing.domain.response.tournament

import xcode.bracketing.domain.enums.TournamentFormat
import xcode.bracketing.domain.enums.TournamentStatus
import xcode.bracketing.domain.enums.TournamentType
import java.util.*

class TournamentDetailResponse {
    var id: Int? = null
    var host = ""
    var description = ""
    var url = ""
    var format: TournamentFormat? = null
    var type: TournamentType? = null
    var participants = 0
    var groupParticipants = 0
    var groupAdvanceParticipants = 0
    var isThirdPlace = false
    var roundStage = 1
    var status: TournamentStatus = TournamentStatus.WAITING
    var teams: MutableList<TeamTournamentDetail> = mutableListOf()
    var groups: MutableList<GroupDetailResponse> = mutableListOf()
    var createdAt: Date? = null
    var startedAt: Date? = null
    var endAt: Date? = null
}

class TeamTournamentDetail {
    var id = 0
    var groupId = 0
    var name = ""
    var captain = ""
    var number = 0
    var isLost = false
}
