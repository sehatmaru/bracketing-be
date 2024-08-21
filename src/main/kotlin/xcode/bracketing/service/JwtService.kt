package xcode.bracketing.service

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Service
import xcode.bracketing.domain.model.User
import xcode.bracketing.utils.CommonUtil.getTomorrowDate
import java.util.*

@Service
class JwtService {

    fun generateToken(user: User): String {
        return Jwts.builder()
            .setSubject(user.id.toString())
            .setIssuedAt(Date())
            .setExpiration(getTomorrowDate())
            .signWith(SignatureAlgorithm.HS256, "xcode")
            .compact()
    }
}