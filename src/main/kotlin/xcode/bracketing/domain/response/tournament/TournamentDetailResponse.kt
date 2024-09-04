package xcode.bracketing.domain.response.tournament

import com.fasterxml.jackson.annotation.JsonFormat
import xcode.bracketing.domain.enums.MatchStage
import xcode.bracketing.domain.enums.TournamentFormat
import xcode.bracketing.domain.enums.TournamentStatus
import xcode.bracketing.domain.enums.TournamentType
import java.util.*

class TournamentDetailResponse {
    var id: Int? = null
    var name = ""
    var host = ""
    var description = ""
    var url = ""
    var format: TournamentFormat? = null
    var type: TournamentType? = null
    var participants = 0
    var groupParticipants = 0
    var groupAdvanceParticipants = 0
    var isThirdPlace = false
    var stage: MatchStage = MatchStage.OTHER
    var status: TournamentStatus = TournamentStatus.WAITING
    var teams: MutableList<TeamTournamentDetail> = mutableListOf()
    var groups: MutableList<GroupDetailResponse> = mutableListOf()

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "UTC")
    var createdAt: Date? = null

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "UTC")
    var startedAt: Date? = null

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "UTC")
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
