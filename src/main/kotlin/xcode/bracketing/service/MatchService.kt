package xcode.bracketing.service

import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import xcode.bracketing.domain.enums.GroupStatus
import xcode.bracketing.domain.enums.MatchStage
import xcode.bracketing.domain.enums.MatchStatus
import xcode.bracketing.domain.enums.TournamentStatus
import xcode.bracketing.domain.model.Group
import xcode.bracketing.domain.model.Match
import xcode.bracketing.domain.model.Team
import xcode.bracketing.domain.model.Tournament
import xcode.bracketing.domain.repository.GroupRepository
import xcode.bracketing.domain.repository.MatchRepository
import xcode.bracketing.domain.repository.TeamRepository
import xcode.bracketing.domain.repository.TournamentRepository
import xcode.bracketing.domain.request.match.MatchScoreRequest
import xcode.bracketing.domain.request.match.TeamMatchScoreRequest
import xcode.bracketing.domain.response.BaseResponse
import xcode.bracketing.domain.response.match.MatchPlayedResponse
import xcode.bracketing.domain.response.match.MatchResponse
import xcode.bracketing.domain.response.match.TeamMatchPlayedResponse
import xcode.bracketing.domain.response.match.TeamMatchResponse
import xcode.bracketing.exception.AppException
import xcode.bracketing.shared.ResponseCode
import java.util.*

@Service
class MatchService @Autowired constructor(
    private val matchRepository: MatchRepository,
    private val groupRepository: GroupRepository,
    private val teamRepository: TeamRepository,
    private val tournamentRepository: TournamentRepository
) {

    var matches: MutableList<Match> = mutableListOf()

    fun generateGroupStageMatches(tournamentId: Int, group: List<Group>): List<Match> {
        val result = ArrayList<Match>()

        group.forEach { e ->
            val teams = teamRepository.findByGroupId(e.id)

            for (i in 0 until teams!!.count()-1) {
                for (j in i+1 until teams.count()) {
                    val match = Match()
                    match.tournamentId = tournamentId
                    match.groupId = e.id
                    match.stage = MatchStage.GROUP
                    match.teamANumber = teams[i]?.number!!
                    match.teamBNumber = teams[j]?.number!!

                    matchRepository.save(match)

                    result.add(match)
                }
            }
        }

        return result
    }

    fun generateFinalStageMatches(tournament: Tournament, groups: List<Group>, teams: List<Team>): List<Match> {
        val result = ArrayList<Match>()
        matches = ArrayList()
        val participants = if (groups.isEmpty()) teams.size else (groups[0].advanceParticipant * groups.size)
        val totalMatches = participants - 1

        var counter = 1
        var teamCounter = 0
        for (i in 0 until  totalMatches) {
            val match = Match()
            match.tournamentId = tournament.id

            when (counter) {
                1 -> if (groups.isEmpty()) match.stage = MatchStage.FINAL
                in 2..3 -> {
                    if (groups.isEmpty()) match.stage = MatchStage.SEMI_FINAL
                    match.nextMatchId = getNextMatch(MatchStage.SEMI_FINAL, MatchStage.FINAL)

                    if (totalMatches == 3) {
                        match.teamANumber = teams[teamCounter].number
                        match.teamBNumber = teams[teamCounter+1].number

                        teamCounter += 2
                    }
                }
                in 4..7 -> {
                    if (groups.isEmpty()) match.stage = MatchStage.QUARTER_FINAL
                    match.nextMatchId = getNextMatch(MatchStage.QUARTER_FINAL, MatchStage.SEMI_FINAL)

                    if (totalMatches == 7) {
                        match.teamANumber = teams[teamCounter].number
                        match.teamBNumber = teams[teamCounter+1].number

                        teamCounter += 2
                    }
                }
                in 8..15 -> {
                    if (groups.isEmpty()) match.stage = MatchStage.TOP_8
                    match.nextMatchId = getNextMatch(MatchStage.TOP_8, MatchStage.QUARTER_FINAL)

                    if (totalMatches == 15) {
                        match.teamANumber = teams[teamCounter].number
                        match.teamBNumber = teams[teamCounter+1].number

                        teamCounter += 2
                    }
                }
                in 16..31 -> {
                    if (groups.isEmpty()) match.stage = MatchStage.TOP_16
                    match.nextMatchId = getNextMatch(MatchStage.TOP_16, MatchStage.TOP_8)

                    if (totalMatches == 31) {
                        match.teamANumber = teams[teamCounter].number
                        match.teamBNumber = teams[teamCounter+1].number

                        teamCounter += 2
                    }
                }
                in 32..63 -> {
                    if (groups.isEmpty()) match.stage = MatchStage.TOP_32
                    match.nextMatchId = getNextMatch(MatchStage.TOP_32, MatchStage.TOP_16)

                    if (totalMatches == 63) {
                        match.teamANumber = teams[teamCounter].number
                        match.teamBNumber = teams[teamCounter+1].number

                        teamCounter += 2
                    }
                }
                in 64..127 -> {
                    if (groups.isEmpty()) match.stage = MatchStage.TOP_64
                    match.nextMatchId = getNextMatch(MatchStage.TOP_64, MatchStage.TOP_32)

                    if (totalMatches == 127) {
                        match.teamANumber = teams[teamCounter].number
                        match.teamBNumber = teams[teamCounter+1].number

                        teamCounter += 2
                    }
                }
                in 128..255 -> {
                    if (groups.isEmpty()) match.stage = MatchStage.TOP_128
                    match.nextMatchId = getNextMatch(MatchStage.TOP_128, MatchStage.TOP_64)

                    if (totalMatches == 255) {
                        match.teamANumber = teams[teamCounter].number
                        match.teamBNumber = teams[teamCounter+1].number

                        teamCounter += 2
                    }
                }
                else -> {
                    if (groups.isEmpty()) match.stage = MatchStage.OTHER
                    match.nextMatchId = getNextMatch(MatchStage.OTHER, MatchStage.TOP_128)
                    match.teamANumber = teams[teamCounter].number
                    match.teamBNumber = teams[teamCounter+1].number

                    teamCounter += 2
                }
            }

            matchRepository.save(match)
            matches.add(match)

            counter++
        }

        if (groups.isEmpty()) {
            when (totalMatches) {
                1 -> tournament.stage = MatchStage.FINAL
                3 -> tournament.stage = MatchStage.SEMI_FINAL
                7 -> tournament.stage = MatchStage.QUARTER_FINAL
                15 -> tournament.stage = MatchStage.TOP_8
                31 -> tournament.stage = MatchStage.TOP_16
                63 -> tournament.stage = MatchStage.TOP_32
                127 -> tournament.stage = MatchStage.TOP_64
                255 -> tournament.stage = MatchStage.TOP_128
            }
        }

        tournamentRepository.save(tournament)

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

    fun getGroupStageMatches(groupId: Int): BaseResponse<List<MatchResponse>> {
        val result = BaseResponse<List<MatchResponse>>()
        val response = mutableListOf<MatchResponse>()

        groupRepository.findById(groupId.toString()).orElseThrow {
            throw AppException(ResponseCode.NOT_FOUND_MESSAGE)
        }

        val matches = matchRepository.findByGroupId(groupId)
        matches!!.forEach { e ->
            val match = MatchResponse()
            BeanUtils.copyProperties(e, match)

            if (e.teamANumber != 0) {
                val teamA = teamRepository.findByNumberAndTournamentId(e.teamANumber, e.tournamentId)
                match.teamA = TeamMatchResponse(teamA!!.id, teamA.name, teamA.number)
            }

            if (e.teamBNumber != 0) {
                val teamB = teamRepository.findByNumberAndTournamentId(e.teamBNumber, e.tournamentId)
                match.teamB = TeamMatchResponse(teamB!!.id, teamB.name, teamB.number)
            }

            response.add(match)
        }

        result.setSuccess(response)

        return result
    }

    fun getTournamentMatches(tournamentId: Int): BaseResponse<List<MatchResponse>> {
        val result = BaseResponse<List<MatchResponse>>()
        val response = mutableListOf<MatchResponse>()

        tournamentRepository.findById(tournamentId.toString()).orElseThrow {
            throw AppException(ResponseCode.NOT_FOUND_MESSAGE)
        }

        val matches = matchRepository.findByTournamentId(tournamentId)
        matches!!.forEach { e ->
            val match = MatchResponse()
            BeanUtils.copyProperties(e, match)

            if (e.teamANumber != 0) {
                val teamA = teamRepository.findByNumberAndTournamentId(e.teamANumber, e.tournamentId)
                match.teamA = TeamMatchResponse(teamA!!.id, teamA.name, teamA.number)
            }

            if (e.teamBNumber != 0) {
                val teamB = teamRepository.findByNumberAndTournamentId(e.teamBNumber, e.tournamentId)
                match.teamB = TeamMatchResponse(teamB!!.id, teamB.name, teamB.number)
            }

            response.add(match)
        }

        result.setSuccess(response)

        return result
    }

    fun playMatch(matchId: Int, request: MatchScoreRequest): BaseResponse<MatchPlayedResponse> {
        val result = BaseResponse<MatchPlayedResponse>()

        val match = matchRepository.findById(matchId.toString()).orElseThrow {
            throw AppException(ResponseCode.NOT_FOUND_MESSAGE)
        }

        if (match!!.isStarted()) throw AppException("This match is already played")
        if (!tournamentRepository.findById(match.tournamentId.toString()).get().isStarted()) throw AppException("This tournament not yet started")

        match.teamAScore = request.teamA!!.score
        match.teamBScore = request.teamB!!.score
        match.status = MatchStatus.FINISHED

        val response = if (match.stage == MatchStage.GROUP) saveGroupStageMatch(match, request.teamA!!, request.teamB!!)
        else saveFinalStageMatch(match, request.teamA!!, request.teamB!!)

        result.setSuccess(response)

        return result
    }

    fun saveGroupStageMatch(match: Match, teamARequest: TeamMatchScoreRequest, teamBRequest: TeamMatchScoreRequest): MatchPlayedResponse {
        val response = MatchPlayedResponse()
        val teamA = teamRepository.findByNumberAndTournamentId(match.teamANumber, match.tournamentId)!!
        val teamB = teamRepository.findByNumberAndTournamentId(match.teamBNumber, match.tournamentId)!!

        teamA.groupPlayed += 1
        teamA.groupScore += teamARequest.score

        teamB.groupPlayed += 1
        teamB.groupScore += teamBRequest.score

        if (teamARequest.isWinner) {
            teamA.groupWins += 1
            teamA.groupPoint += 3

            teamB.groupLoses += 1
            response.winner = TeamMatchPlayedResponse(teamA.id, teamA.name, teamA.number)
            match.winner = teamA.number
        } else if (teamBRequest.isWinner) {
            teamB.groupWins += 1
            teamB.groupPoint += 3

            teamA.groupLoses += 1
            response.winner = TeamMatchPlayedResponse(teamB.id, teamB.name, teamB.number)
            match.winner = teamB.number
        } else {
            teamA.groupDraws += 1
            teamA.groupPoint += 1
            teamB.groupDraws += 1
            teamB.groupPoint += 1
        }

        matchRepository.save(match)
        teamRepository.saveAll(listOf(teamA, teamB))

        checkGroupStatus(match.groupId!!)
        checkTournamentStage(match.tournamentId)

        return response
    }

    fun checkGroupStatus(groupId: Int) {
        val group = groupRepository.findById(groupId.toString()).orElseThrow {
            throw AppException(ResponseCode.NOT_FOUND_MESSAGE)
        }

        val matchRemaining = matchRepository.findByGroupIdAndStatus(groupId, MatchStatus.WAITING)?.size ?: 0

        if (matchRemaining == 0) {
            group!!.status = GroupStatus.FINISHED
            group.endedAt = Date()

            groupRepository.save(group)
        }
    }

    fun saveFinalStageMatch(match: Match, teamARequest: TeamMatchScoreRequest, teamBRequest: TeamMatchScoreRequest): MatchPlayedResponse {
        val response = MatchPlayedResponse()
        val sameBracketMatchId = matchRepository.findByNextMatchId(match.nextMatchId)?.firstOrNull { e -> e.id != match.id }?.id ?: 0
        val nextMatch = matchRepository.findById(match.nextMatchId.toString())
        val teamA = teamRepository.findByNumberAndTournamentId(match.teamANumber, match.tournamentId)!!
        val teamB = teamRepository.findByNumberAndTournamentId(match.teamBNumber, match.tournamentId)!!

        match.winner = if (teamARequest.isWinner) teamA.number else teamB.number
        response.winner = if (teamARequest.isWinner) TeamMatchPlayedResponse(teamA.id, teamA.name, teamA.number) else TeamMatchPlayedResponse(teamB.id, teamB.name, teamB.number)

        if (teamARequest.isWinner) {
            if (match.stage != MatchStage.FINAL) {
                if (match.id < sameBracketMatchId) nextMatch.get().teamANumber = teamA.number
                else nextMatch.get().teamBNumber = teamA.number
            }
        } else if (teamBRequest.isWinner) {
            if (match.stage != MatchStage.FINAL) {
                if (match.id < sameBracketMatchId) nextMatch.get().teamANumber = teamB.number
                else nextMatch.get().teamBNumber = teamB.number
            }
        }

        matchRepository.save(match)
        teamRepository.saveAll(listOf(teamA, teamB))

        checkTournamentStage(match.tournamentId)

        return response
    }

    //    TODO
    fun checkTournamentStage(tournamentId: Int) {
        val tournament = tournamentRepository.findById(tournamentId.toString()).orElseThrow {
            throw AppException(ResponseCode.NOT_FOUND_MESSAGE)
        }

        val currentStage = tournament!!.stage
        val matchRemaining = matchRepository.findByStageAndTournamentIdAndStatus(currentStage, tournamentId, MatchStatus.WAITING)

        if (matchRemaining == null) {
            when (currentStage) {
                MatchStage.GROUP -> {
                    val totalGroup = groupRepository.findByTournamentId(tournamentId)?.size ?: 0
                    val totalTeams = totalGroup * tournament.groupAdvanceParticipants

                    when (totalTeams) {
                        2 -> tournament.stage = MatchStage.FINAL
                        4 -> tournament.stage = MatchStage.SEMI_FINAL
                        8 -> tournament.stage = MatchStage.QUARTER_FINAL
                        16 -> tournament.stage = MatchStage.TOP_8
                        32 -> tournament.stage = MatchStage.TOP_16
                        64 -> tournament.stage = MatchStage.TOP_32
                        128 -> tournament.stage = MatchStage.TOP_64
                        256 -> tournament.stage = MatchStage.TOP_128
                        else -> tournament.stage = MatchStage.OTHER
                    }
                }
                MatchStage.OTHER -> tournament.stage = MatchStage.TOP_128
                MatchStage.TOP_128 -> tournament.stage = MatchStage.TOP_64
                MatchStage.TOP_64 -> tournament.stage = MatchStage.TOP_32
                MatchStage.TOP_32 -> tournament.stage = MatchStage.TOP_16
                MatchStage.TOP_16 -> tournament.stage = MatchStage.TOP_8
                MatchStage.TOP_8 -> tournament.stage = MatchStage.QUARTER_FINAL
                MatchStage.QUARTER_FINAL -> tournament.stage = MatchStage.SEMI_FINAL
                MatchStage.SEMI_FINAL -> tournament.stage = MatchStage.FINAL
                MatchStage.FINAL -> tournament.status = TournamentStatus.FINISHED
            }
        }

        tournamentRepository.save(tournament)
    }
}