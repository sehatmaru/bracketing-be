package xcode.bracketing.domain.model

import jakarta.persistence.*
import org.hibernate.annotations.DynamicUpdate
import xcode.bracketing.domain.enums.MatchStage
import xcode.bracketing.domain.enums.TournamentFormat
import xcode.bracketing.domain.enums.TournamentStatus
import xcode.bracketing.domain.enums.TournamentType
import java.util.*

@Entity
@Table(name = "t_tournament")
@DynamicUpdate
class Tournament {

    @Id
    @Column(name = "id", length = 36)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id = 0

    @Column(name = "name")
    var name = ""

    @Column(name = "created_by")
    var createdBy = 0

    @Column(name = "host")
    var host = ""

    @Column(name = "description")
    var description = ""

    @Column(name = "url")
    var url = ""

    @Column(name = "format")
    var format: TournamentFormat? = null

    @Column(name = "type")
    var type: TournamentType? = null

    @Column(name = "participants")
    var participants = 0

    @Column(name = "group_participants")
    var groupParticipants = 0

    @Column(name = "group_advance_participants")
    var groupAdvanceParticipants = 0

    @Column(name = "is_third_place")
    var isThirdPlace = false

    @Column(name = "stage")
    var stage: MatchStage = MatchStage.OTHER

    @Column(name = "status")
    var status: TournamentStatus = TournamentStatus.WAITING

    @Column(name = "started_at")
    var startedAt: Date? = null

    @Column(name = "created_at")
    var createdAt: Date? = null

    @Column(name = "updated_at")
    var updatedAt: Date? = null

    @Column(name = "deleted_at")
    var deletedAt: Date? = null

    fun isGroupFormat(): Boolean {
        return format == TournamentFormat.DOUBLE_ELIMINATION && type == TournamentType.TWO_STAGE
    }

    fun isStarted(): Boolean {
        return status != TournamentStatus.WAITING
    }
}