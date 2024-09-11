package xcode.bracketing.domain.request.tournament

import xcode.bracketing.domain.enums.TournamentFormat

class CreateTournamentRequest {

    var name = ""
    var host = ""
    var description = ""
    var url = ""
    var format: TournamentFormat? = null
    var isHomeAway = false
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