package xcode.bracketing.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import xcode.bracketing.domain.request.tournament.CreateTournamentRequest
import xcode.bracketing.domain.response.BaseResponse
import xcode.bracketing.domain.response.tournament.CreateTournamentResponse
import xcode.bracketing.domain.response.tournament.GroupDetailResponse
import xcode.bracketing.domain.response.tournament.TournamentDetailResponse
import xcode.bracketing.domain.response.tournament.TournamentListResponse
import xcode.bracketing.service.TournamentService

@RestController
@RequestMapping(value = ["api/tournament"])
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

    @GetMapping("/list")
    fun getTournamentList(): ResponseEntity<BaseResponse<List<TournamentListResponse>>> {
        val response: BaseResponse<List<TournamentListResponse>> = tournamentService.getTournamentList()

        return ResponseEntity
            .status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(response)
    }

    @GetMapping("/group/detail/{groupId}")
    fun getGroupDetail(@PathVariable("groupId") @Validated id: Int): ResponseEntity<BaseResponse<GroupDetailResponse>> {
        val response: BaseResponse<GroupDetailResponse> = tournamentService.getGroupDetail(id)

        return ResponseEntity
            .status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(response)
    }

    @GetMapping("/detail/{tournamentId}")
    fun getTournamentDetail(@PathVariable("tournamentId") @Validated id: Int): ResponseEntity<BaseResponse<TournamentDetailResponse>> {
        val response: BaseResponse<TournamentDetailResponse> = tournamentService.getTournamentDetail(id)

        return ResponseEntity
            .status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(response)
    }

    @PostMapping("/start/{tournamentId}")
    fun startTournament(@PathVariable("tournamentId") @Validated id: Int): ResponseEntity<BaseResponse<Boolean>> {
        val response: BaseResponse<Boolean> = tournamentService.startTournament(id)

        return ResponseEntity
            .status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(response)
    }

    @PostMapping("/teams/randomize/{tournamentId}")
    fun randomizeTournament(@PathVariable("tournamentId") @Validated id: Int): ResponseEntity<BaseResponse<Boolean>> {
        val response: BaseResponse<Boolean> = tournamentService.randomizeTeam(id)

        return ResponseEntity
            .status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(response)
    }
}