package xcode.bracketing.service

import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import xcode.bracketing.domain.enums.MatchStage
import xcode.bracketing.domain.model.Group
import xcode.bracketing.domain.model.Match
import xcode.bracketing.domain.model.Team
import xcode.bracketing.domain.repository.GroupRepository
import xcode.bracketing.domain.repository.MatchRepository
import xcode.bracketing.domain.repository.TeamRepository
import xcode.bracketing.domain.repository.TournamentRepository
import xcode.bracketing.domain.response.BaseResponse
import xcode.bracketing.domain.response.match.MatchResponse
import xcode.bracketing.exception.AppException
import xcode.bracketing.shared.ResponseCode
import kotlin.math.floor

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

    fun generateFinalStageMatches(tournamentId: Int, groups: List<Group>, teams: List<Team>): List<Match> {
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

    fun getGroupStageMatches(id: Int): BaseResponse<List<MatchResponse>> {
        val result = BaseResponse<List<MatchResponse>>()
        val response = mutableListOf<MatchResponse>()

        groupRepository.findById(id.toString()).orElseThrow {
            throw AppException(ResponseCode.NOT_FOUND_MESSAGE)
        }

        val matches = matchRepository.findByGroupId(id)
        matches!!.forEach { e ->
            val match = MatchResponse()
            BeanUtils.copyProperties(e, match)

            response.add(match)
        }

        result.setSuccess(response)

        return result
    }

    fun getTournamentMatches(id: Int): BaseResponse<List<MatchResponse>> {
        val result = BaseResponse<List<MatchResponse>>()
        val response = mutableListOf<MatchResponse>()

        tournamentRepository.findById(id.toString()).orElseThrow {
            throw AppException(ResponseCode.NOT_FOUND_MESSAGE)
        }

        val matches = matchRepository.findByTournamentId(id)
        matches!!.forEach { e ->
            val match = MatchResponse()
            BeanUtils.copyProperties(e, match)

            response.add(match)
        }

        result.setSuccess(response)

        return result
    }
}