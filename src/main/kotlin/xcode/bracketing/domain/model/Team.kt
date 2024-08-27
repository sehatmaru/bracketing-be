package xcode.bracketing.domain.model

import jakarta.persistence.*
import org.hibernate.annotations.DynamicUpdate
import java.util.*

@Entity
@Table(name = "t_team")
@DynamicUpdate
class Team {

    @Id
    @Column(name = "id", length = 36)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id = 0

    @Column(name = "tournament_id")
    var tournamentId = 0

    @Column(name = "group_id")
    var groupId = 0

    @Column(name = "name")
    var name = ""

    @Column(name = "captain")
    var captain = ""

    @Column(name = "number")
    var number = 0

    @Column(name = "is_lost")
    var isLost = false

    @Column(name = "group_wins")
    var groupWins = 0

    @Column(name = "group_draws")
    var groupDraws = 0

    @Column(name = "group_loses")
    var groupLoses = 0

    @Column(name = "group_score")
    var groupScore = 0

    @Column(name = "group_played")
    var groupPlayed = 0

    @Column(name = "group_point")
    var groupPoint = 0

    @Column(name = "created_at")
    var createdAt: Date? = null

    @Column(name = "updated_at")
    var updatedAt: Date? = null

    @Column(name = "deleted_at")
    var deletedAt: Date? = null

}