package xcode.bracketing.domain.response.tournament

import com.fasterxml.jackson.annotation.JsonFormat
import xcode.bracketing.domain.enums.GroupStatus
import java.util.*

class TournamentListResponse {
    var id: Int? = null
    var name = ""
    var status: GroupStatus = GroupStatus.WAITING

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "UTC")
    var createdAt: Date? = null
}