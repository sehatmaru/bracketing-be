package xcode.bracketing.service

import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import xcode.bracketing.domain.enums.*
import xcode.bracketing.domain.model.*
import xcode.bracketing.domain.repository.*
import xcode.bracketing.domain.request.tournament.CreateTournamentRequest
import xcode.bracketing.domain.request.tournament.GroupSettingRequest
import xcode.bracketing.domain.request.tournament.TeamRequest
import xcode.bracketing.domain.response.BaseResponse
import xcode.bracketing.domain.response.tournament.*
import xcode.bracketing.exception.AppException
import xcode.bracketing.shared.ResponseCode
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.floor

@Service
class TournamentService @Autowired constructor(
    private val tournamentRepository: TournamentRepository,
    private val matchRepository: MatchRepository,
    private val groupRepository: GroupRepository,
    private val teamRepository: TeamRepository
) {

    var matches: MutableList<Match> = mutableListOf()

    fun createTournament(request: CreateTournamentRequest): BaseResponse<CreateTournamentResponse> {
        val baseResponse = BaseResponse<CreateTournamentResponse>()

        val tournament = Tournament()
        BeanUtils.copyProperties(request, tournament)
        tournament.createdAt = Date()
        tournament.participants = request.teams.size

        tournamentRepository.save(tournament)

        val teams = generateTeams(tournament.id, request.teams)
        var groups: List<Group> = ArrayList()

        if (request.type == TournamentType.TWO_STAGE) {
            groups = generateGroups(tournament.id, request.isRandomize, request.groupSetting!!,  teams)
            generateGroupMatches(tournament.id, groups)
        }

        generateFinalMatches(tournament.id, groups, teams)

        baseResponse.setSuccess(CreateTournamentResponse(tournament.id, tournament.name))

        return baseResponse
    }

    fun generateTeams(tournamentId: Int, teams: List<TeamRequest>): List<Team> {
        val result = ArrayList<Team>()
        var number = 1

        teams.forEach { e ->
            val team = Team()
            team.tournamentId = tournamentId
            team.name = e.name
            team.number = number
            team.createdAt = Date()

            teamRepository.save(team)
            result.add(team)

            number++
        }

        return result
    }

    fun generateGroups(tournamentId: Int, isRandomize: Boolean, groupSetting: GroupSettingRequest, teams: List<Team>): List<Group> {
        val result = ArrayList<Group>()

        val participants = teams.size
        var groupNumbers = participants/groupSetting.groupParticipants
        if (participants%groupNumbers != 0) groupNumbers++

        if (isRandomize) teams.shuffled()

        var i = 0
        var k = 0
        while (i < groupNumbers) {
            val group = Group()
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

    fun generateGroupMatches(tournamentId: Int, group: List<Group>): List<Match> {
        val result = ArrayList<Match>()

        group.forEach { e ->
            val teams = teamRepository.findByGroupId(e.id)

            for (i in 0 until teams!!.count()-1) {
                for (j in i+1 until teams.count()) {
                    val match = Match()
                    match.tournamentId = tournamentId
                    match.groupId = e.id
                    match.stage = MatchStage.GROUP
                    match.teamAId = teams[i]?.id!!
                    match.teamBId = teams[j]?.id!!

                    matchRepository.save(match)

                    result.add(match)
                }
            }
        }

        return result
    }

    fun generateFinalMatches(tournamentId: Int, groups: List<Group>, teams: List<Team>): List<Match> {
        val result = ArrayList<Match>()
        matches = ArrayList()
        val participants = if (groups.isEmpty()) teams.size else (groups[0].advanceParticipant * groups.size)
        val totalMatches = floor((participants / 2.0)).toInt()-1

        var counter = 1
        for (i in 0 until  totalMatches) {
            val match = Match()
            match.tournamentId = tournamentId

            when (counter) {
                1 -> match.stage = MatchStage.FINAL
                in 2..3 -> {
                    match.stage = MatchStage.SEMI_FINAL
                    match.nextMatchId = getNextMatch(MatchStage.SEMI_FINAL, MatchStage.FINAL)
                }
                in 4..7 -> {
                    match.stage = MatchStage.QUARTER_FINAL
                    match.nextMatchId = getNextMatch(MatchStage.QUARTER_FINAL, MatchStage.SEMI_FINAL)
                }
                in 8..15 -> {
                    match.stage = MatchStage.TOP_8
                    match.nextMatchId = getNextMatch(MatchStage.TOP_8, MatchStage.QUARTER_FINAL)
                }
                in 16..31 -> {
                    match.stage = MatchStage.TOP_16
                    match.nextMatchId = getNextMatch(MatchStage.TOP_16, MatchStage.TOP_8)
                }
                in 32..63 -> {
                    match.stage = MatchStage.TOP_32
                    match.nextMatchId = getNextMatch(MatchStage.TOP_32, MatchStage.TOP_16)
                }
                in 64..127 -> {
                    match.stage = MatchStage.TOP_64
                    match.nextMatchId = getNextMatch(MatchStage.TOP_64, MatchStage.TOP_32)
                }
                in 128..255 -> {
                    match.stage = MatchStage.TOP_128
                    match.nextMatchId = getNextMatch(MatchStage.TOP_128, MatchStage.TOP_64)
                }
                else -> {
                    match.stage = MatchStage.OTHER
                    match.nextMatchId = getNextMatch(MatchStage.OTHER, MatchStage.TOP_128)
                }
            }

            matchRepository.save(match)
            matches.add(match)

            counter++
        }

        return result
    }

    fun getNextMatch(currentStage: MatchStage, nextStage: MatchStage): Int {
        val nextMatches = matches.filter { e -> e.stage == nextStage }

        for (i in nextMatches.indices) {
            val counter = matches.count { e -> e.nextMatchId == nextMatches[i].id }

            if (counter >= 2) continue

            return nextMatches[i].id
        }

        return 0
    }

    fun getGroupDetail(id: Int): BaseResponse<GroupDetailResponse> {
        val result: BaseResponse<GroupDetailResponse> = BaseResponse()
        val response = initGroupDetail(id)

        result.setSuccess(response)

        return result
    }

    fun initGroupDetail(id: Int): GroupDetailResponse {
        val response = GroupDetailResponse()

        val group = groupRepository.findById(id.toString()).orElseThrow {
            throw AppException(ResponseCode.NOT_FOUND_MESSAGE)
        }

        response.id = group!!.id
        response.tournamentId = group.tournamentId
        response.status = group.status
        response.startedAt = group.startedAt
        response.endedAt = group.endedAt

        val teams = teamRepository.findByGroupId(group.id)

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

            response.teams.add(teamResponse)
        }

        return response
    }

    fun getTournamentDetail(id: Int): BaseResponse<TournamentDetailResponse> {
        val result: BaseResponse<TournamentDetailResponse> = BaseResponse()
        val response = TournamentDetailResponse()

        val tournament = tournamentRepository.findById(id.toString()).orElseThrow {
            throw AppException(ResponseCode.NOT_FOUND_MESSAGE)
        }

        BeanUtils.copyProperties(tournament!!, response)

        val teams = teamRepository.findByTournamentId(tournament.id)

        teams?.forEach { e ->
            val team = TeamTournamentDetail()
            BeanUtils.copyProperties(e!!, team)

            response.teams.add(team)
        }

        if (tournament.isGroupStage()) {
            val groups = groupRepository.findByTournamentId(tournament.id)

            groups!!.forEach { e ->
                response.groups.add(initGroupDetail(e.id))
            }
        }

        result.setSuccess(response)

        return result
    }

    fun startTournament(id: Int): BaseResponse<Boolean> {
        val tournament = tournamentRepository.findById(id.toString()).orElseThrow {
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

        if (tournament.isGroupStage()) {
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
}