package xcode.bracketing.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import xcode.bracketing.domain.model.Team

@Repository
interface TeamRepository : JpaRepository<Team?, String?> {
    fun findTeamByGroupId(groupId: Int): List<Team?>?
}