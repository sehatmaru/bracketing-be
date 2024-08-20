package xcode.bracketing.domain.model

import jakarta.persistence.*
import lombok.AllArgsConstructor
import lombok.Builder
import lombok.Data
import lombok.NoArgsConstructor
import org.hibernate.annotations.DynamicUpdate
import java.util.*


import xcode.bracketing.utils.CommonUtil.generateSecureId
import xcode.bracketing.utils.CommonUtil.getTomorrowDate

@Data
@Builder
@Entity
@Table(name = "t_token")
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
class Token {

    @Id
    @Column(name = "id", length = 36)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private val id = 0

    @Column(name = "secure_id")
    private var secureId: String? = null

    @Column(name = "auth_secure_id")
    private var authSecureId: String? = null

    @Column(name = "token")
    private var token: String? = null

    @Column(name = "created_at")
    private var createdAt: Date? = null

    @Column(name = "expire_at")
    private var expireAt: Date? = null

    fun TokenModel(token: String?, authSecureId: String?) {
        this.secureId = generateSecureId()
        this.token = token
        this.authSecureId = authSecureId
        this.createdAt = Date()
        this.expireAt = getTomorrowDate()
    }

    fun isValid(): Boolean {
        return !expireAt!!.before(Date())
    }
}