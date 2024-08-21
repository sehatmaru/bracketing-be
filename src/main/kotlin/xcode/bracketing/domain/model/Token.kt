package xcode.bracketing.domain.model

import jakarta.persistence.*
import org.hibernate.annotations.DynamicUpdate
import java.util.*

@Entity
@Table(name = "t_token")
@DynamicUpdate
class Token(@Column(name = "token") var token: String, id: Int, expireAt: Date) {

    @Id
    @Column(name = "id", length = 36)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private val id = 0

    @Column(name = "user_id")
    var userId = id

    @Column(name = "created_at")
    var createdAt: Date? = null

    @Column(name = "expire_at")
    private var expireAt: Date? = expireAt

    fun isValid(): Boolean {
        return !expireAt!!.before(Date())
    }
}