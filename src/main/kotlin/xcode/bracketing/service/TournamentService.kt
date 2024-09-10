package xcode.bracketing.service

import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import xcode.bracketing.domain.enums.GroupStatus
import xcode.bracketing.domain.enums.MatchStage
import xcode.bracketing.domain.enums.TournamentStatus
import xcode.bracketing.domain.enums.TournamentType
import xcode.bracketing.domain.model.CurrentAuth
import xcode.bracketing.domain.model.Group
import xcode.bracketing.domain.model.Team
import xcode.bracketing.domain.model.Tournament
import xcode.bracketing.domain.repository.GroupRepository
import xcode.bracketing.domain.repository.TeamRepository
import xcode.bracketing.domain.repository.TournamentRepository
import xcode.bracketing.domain.request.tournament.CreateTournamentRequest
import xcode.bracketing.domain.request.tournament.GroupSettingRequest
import xcode.bracketing.domain.request.tournament.TeamRequest
import xcode.bracketing.domain.response.BaseResponse
import xcode.bracketing.domain.response.tournament.*
import xcode.bracketing.exception.AppException
import xcode.bracketing.shared.ResponseCode
import java.util.*
import kotlin.collections.ArrayList

@Service
class TournamentService @Autowired constructor(
    private val matchService: MatchService,
    private val tournamentRepository: TournamentRepository,
    private val groupRepository: GroupRepository,
    private val teamRepository: TeamRepository
) {

    fun createTournament(request: CreateTournamentRequest): BaseResponse<CreateTournamentResponse> {
        val baseResponse = BaseResponse<CreateTournamentResponse>()

        val tournament = Tournament()
        BeanUtils.copyProperties(request, tournament)
        tournament.createdAt = Date()
        tournament.createdBy = CurrentAuth.get().userId
        tournament.participants = request.teams.size
        tournament.groupParticipants = request.groupSetting?.groupParticipants ?: 0
        tournament.groupAdvanceParticipants = request.groupSetting?.groupAdvanceParticipants ?: 0
        if (tournament.isGroupFormat()) tournament.stage = MatchStage.GROUP

        tournamentRepository.save(tournament)

        val teams = generateTeams(tournament.id, request.isRandomize, request.teams)
        var groups: List<Group> = ArrayList()

        if (request.type == TournamentType.TWO_STAGE) {
            groups = generateGroups(tournament.id, request.groupSetting!!,  teams)
            matchService.generateGroupStageMatches(tournament.id, groups)
        }

        matchService.generateFinalStageMatches(tournament, groups, teams)

        baseResponse.setSuccess(CreateTournamentResponse(tournament.id, tournament.name))

        return baseResponse
    }

    fun generateTeams(tournamentId: Int, isRandomize: Boolean, teams: List<TeamRequest>): List<Team> {
        val finalTeams = if (isRandomize) teams.shuffled() else teams
        val result = ArrayList<Team>()
        var number = 1

        finalTeams.forEach { e ->
            val team = Team()
            team.tournamentId = tournamentId
            team.name = e.name
            team.captain = e.captain
            team.number = number
            team.createdAt = Date()

            teamRepository.save(team)
            result.add(team)

            number++
        }

        return result
    }

    fun generateGroups(tournamentId: Int, groupSetting: GroupSettingRequest, teams: List<Team>): List<Group> {
        val result = ArrayList<Group>()

        val participants = teams.size
        var groupNumbers = participants/groupSetting.groupParticipants
        if (participants%groupNumbers != 0) groupNumbers++

        var i = 0
        var k = 0
        val name = 'A'.code
        while (i < groupNumbers) {
            val group = Group()
            group.name = "Group " + (name + i).toChar().toString()
            group.tournamentId = tournamentId
            group.advanceParticipant = groupSetting.groupAdvanceParticipants

            groupRepository.save(group)

            var number = 0
            while (number < groupSetting.groupParticipants) {
                teams[k].groupId = group.id

                number++
                k++
            }

            teamRepository.saveAll(teams)

            result.add(group)
            i++
        }

        return result
    }

    fun getGroupDetail(groupId: Int): BaseResponse<GroupDetailResponse> {
        val result: BaseResponse<GroupDetailResponse> = BaseResponse()
        val response = initGroupDetail(groupId)

        result.setSuccess(response)

        return result
    }

    fun initGroupDetail(groupId: Int): GroupDetailResponse {
        val response = GroupDetailResponse()

        val group = groupRepository.findById(groupId.toString()).orElseThrow {
            throw AppException(ResponseCode.NOT_FOUND_MESSAGE)
        }

        BeanUtils.copyProperties(group!!, response)

        val teams = teamRepository.findByGroupIdOrderByGroupPointDesc(group.id)

        teams?.forEach { e ->
            val teamResponse = TeamGroupResponse()
            teamResponse.id = e!!.id
            teamResponse.name = e.name
            teamResponse.played = e.groupPlayed
            teamResponse.wins = e.groupWins
            teamResponse.loses = e.groupLoses
            teamResponse.draws = e.groupDraws
            teamResponse.score = e.groupScore
            teamResponse.point = e.groupPoint
            teamResponse.number = e.number

            response.teams.add(teamResponse)
        }

        return response
    }

    fun getTournamentList(): BaseResponse<List<TournamentListResponse>> {
        val result: BaseResponse<List<TournamentListResponse>> = BaseResponse()
        val response = mutableListOf<TournamentListResponse>()

        val tournamentList = tournamentRepository.findByCreatedBy(CurrentAuth.get().userId)

        tournamentList!!.map { e ->
            response.add(TournamentListResponse().apply {
                BeanUtils.copyProperties(e, this)
            })
        }

        result.setSuccess(response)

        return result
    }

    fun getTournamentDetail(tournamentId: Int): BaseResponse<TournamentDetailResponse> {
        val result: BaseResponse<TournamentDetailResponse> = BaseResponse()
        val response = TournamentDetailResponse()

        val tournament = tournamentRepository.findById(tournamentId.toString()).orElseThrow {
            throw AppException(ResponseCode.NOT_FOUND_MESSAGE)
        }

        BeanUtils.copyProperties(tournament!!, response)

        val teams = teamRepository.findByTournamentId(tournament.id)

        teams?.forEach { e ->
            val team = TeamTournamentDetail()
            BeanUtils.copyProperties(e!!, team)

            response.teams.add(team)
        }

        if (tournament.isGroupFormat()) {
            val groups = groupRepository.findByTournamentId(tournament.id)

            groups!!.forEach { e ->
                response.groups.add(initGroupDetail(e.id))
            }
        }

        result.setSuccess(response)

        return result
    }

    fun startTournament(tournamentId: Int): BaseResponse<Boolean> {
        val tournament = tournamentRepository.findById(tournamentId.toString()).orElseThrow {
            throw AppException(ResponseCode.NOT_FOUND_MESSAGE)
        }

        if (tournament!!.isStarted()) {
            throw AppException("Tournament already started.")
        }

        val startDate = Date()

        tournament.status = TournamentStatus.ON_PROGRESS
        tournament.startedAt = startDate
        tournament.updatedAt = startDate
        tournamentRepository.save(tournament)

        if (tournament.isGroupFormat()) {
            val groups = groupRepository.findByTournamentId(tournament.id)

            groups!!.forEach { e ->
                e.status = GroupStatus.ON_PROGRESS
                e.startedAt = startDate
            }

            groupRepository.saveAll(groups)
        }

        val result = BaseResponse<Boolean>()
        result.setSuccess(true)

        return result
    }

    fun randomizeTeam(tournamentId: Int): BaseResponse<Boolean> {
        val result: BaseResponse<Boolean> = BaseResponse()

        val tournament = tournamentRepository.findById(tournamentId.toString()).orElseThrow {
            throw AppException(ResponseCode.NOT_FOUND_MESSAGE)
        }

        if (tournament!!.isStarted()) throw AppException("Tournament already started.")

        val teams = teamRepository.findByTournamentIdOrderByGroupId(tournamentId)
        var groupId = teams!![0]?.groupId

        val finalTeams = teams.shuffled()

        var number = 1
        var counter = 0
        var groupCounter = 0
        finalTeams.forEach { e ->
            e!!.number = number
            if (tournament.isGroupFormat()) {
                e.groupId = groupId!!
                groupCounter++

                if (groupCounter == tournament.groupParticipants) {
                    groupCounter = 0
                    groupId++
                }
            }

            if (counter < tournament.groupParticipants) counter ++ else counter = 0

            number++
        }

        teamRepository.saveAll(finalTeams)

        result.setSuccess(true)

        return result
    }

}