package xcode.bracketing.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import xcode.bracketing.domain.model.User

@Repository
interface UserRepository : JpaRepository<User?, String?> {
    fun findByUsernameAndDeletedAtIsNull(username: String): User?
}