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

    @Column(name = "tournament_id")
    var tournamentId = 0

    @Column(name = "participants_id")
    var participantsId = ""

    @Column(name = "participants_score")
    var participantsScore = ""

    @Column(name = "status")
    var status: GroupStatus? = null

    @Column(name = "start_at")
    var startAt: Date? = null

    @Column(name = "end_at")
    var endAt: Date? = null

}