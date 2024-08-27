package xcode.bracketing.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import xcode.bracketing.domain.response.BaseResponse
import xcode.bracketing.domain.response.match.MatchResponse
import xcode.bracketing.service.MatchService

@RestController
@RequestMapping(value = ["match"])
class MatchApi @Autowired constructor(
    private val matchService: MatchService
) {

    @GetMapping("/tournament/{id}")
    fun getTournamentMatches(@PathVariable("id") @Validated id: Int): ResponseEntity<BaseResponse<List<MatchResponse>>> {
        val response: BaseResponse<List<MatchResponse>> = matchService.getTournamentMatches(id)

        return ResponseEntity
            .status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(response)
    }

    @GetMapping("/group/{id}")
    fun getGroupStageMatches(@PathVariable("id") @Validated id: Int): ResponseEntity<BaseResponse<List<MatchResponse>>> {
        val response: BaseResponse<List<MatchResponse>> = matchService.getGroupStageMatches(id)

        return ResponseEntity
            .status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(response)
    }

}