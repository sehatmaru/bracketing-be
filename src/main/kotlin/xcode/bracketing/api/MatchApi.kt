package xcode.bracketing.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import xcode.bracketing.domain.request.match.MatchScoreRequest
import xcode.bracketing.domain.response.BaseResponse
import xcode.bracketing.domain.response.match.MatchPlayedResponse
import xcode.bracketing.domain.response.match.MatchResponse
import xcode.bracketing.service.MatchService

@RestController
@RequestMapping(value = ["api/match"])
class MatchApi @Autowired constructor(
    private val matchService: MatchService
) {

    @GetMapping("/tournament/{tournamentId}")
    fun getTournamentMatches(@PathVariable("tournamentId") @Validated id: Int): ResponseEntity<BaseResponse<List<MatchResponse>>> {
        val response: BaseResponse<List<MatchResponse>> = matchService.getTournamentMatches(id)

        return ResponseEntity
            .status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(response)
    }

    @GetMapping("/group/{groupId}")
    fun getGroupStageMatches(@PathVariable("groupId") @Validated id: Int): ResponseEntity<BaseResponse<List<MatchResponse>>> {
        val response: BaseResponse<List<MatchResponse>> = matchService.getGroupStageMatches(id)

        return ResponseEntity
            .status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(response)
    }

    @PostMapping("/play/{matchId}")
    fun login(
        @PathVariable("matchId") @Validated id: Int,
        @RequestBody @Validated request: MatchScoreRequest
    ): ResponseEntity<BaseResponse<MatchPlayedResponse>> {
        val response: BaseResponse<MatchPlayedResponse> = matchService.playMatch(id, request)

        return ResponseEntity
            .status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(response)
    }

}