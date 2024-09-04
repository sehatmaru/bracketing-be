package xcode.bracketing.domain.model

import jakarta.persistence.*
import org.hibernate.annotations.DynamicUpdate
import xcode.bracketing.domain.enums.GroupStatus
import java.util.*

@Entity
@Table(name = "t_group")
@DynamicUpdate
class Group {

    @Id
    @Column(name = "id", length = 36)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id = 0

    @Column(name = "name")
    var name = ""

    @Column(name = "tournament_id")
    var tournamentId = 0

    @Column(name = "advance_participant")
    var advanceParticipant = 0

    @Column(name = "status")
    var status: GroupStatus = GroupStatus.WAITING

    @Column(name = "started_at")
    var startedAt: Date? = null

    @Column(name = "ended_at")
    var endedAt: Date? = null
}