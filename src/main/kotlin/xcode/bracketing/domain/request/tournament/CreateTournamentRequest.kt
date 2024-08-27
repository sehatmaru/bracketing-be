package xcode.bracketing.domain.request.tournament

import xcode.bracketing.domain.enums.TournamentFormat
import xcode.bracketing.domain.enums.TournamentType

class CreateTournamentRequest {

    var name = ""
    var host = ""
    var description = ""
    var url = ""
    var format: TournamentFormat? = null
    var type: TournamentType? = null
    var isThirdPlace = false
    var roundStage = 1
    var isRandomize = false
    var teams: List<TeamRequest> = ArrayList()
    var groupSetting: GroupSettingRequest? = null

}

class TeamRequest {
    var name = ""
    var captain = ""
}

class GroupSettingRequest {
    var groupParticipants = 0
    var groupAdvanceParticipants = 0
}