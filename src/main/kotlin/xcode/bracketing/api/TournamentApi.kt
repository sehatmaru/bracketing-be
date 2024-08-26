package xcode.bracketing.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import xcode.bracketing.domain.request.tournament.CreateTournamentRequest
import xcode.bracketing.domain.response.BaseResponse
import xcode.bracketing.domain.response.tournament.CreateTournamentResponse
import xcode.bracketing.service.TournamentService

@RestController
@RequestMapping(value = ["tournament"])
class TournamentApi @Autowired constructor(
    private val tournamentService: TournamentService
) {

    @PostMapping("/create")
    fun login(@RequestBody @Validated request: CreateTournamentRequest): ResponseEntity<BaseResponse<CreateTournamentResponse>> {
        val response: BaseResponse<CreateTournamentResponse> = tournamentService.createTournament(request)

        return ResponseEntity
            .status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(response)
    }

}