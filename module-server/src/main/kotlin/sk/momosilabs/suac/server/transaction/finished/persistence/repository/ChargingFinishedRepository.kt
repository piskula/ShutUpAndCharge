package sk.momosilabs.suac.server.transaction.finished.persistence.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sk.momosilabs.suac.server.transaction.finished.persistence.entity.ChargingFinishedEntity
import java.math.BigDecimal

@Repository
interface ChargingFinishedRepository: JpaRepository<ChargingFinishedEntity, Long> {

    fun findAllByAccountIdKeycloakAndPriceLessThan(userId: String, lessThan: BigDecimal, pageable: Pageable): Page<ChargingFinishedEntity>

    @Query("""
        SELECT new kotlin.Pair(
            trxFinished.account.idKeycloak,
            COALESCE(SUM(trxFinished.price), 0)
        )
        FROM #{#entityName} trxFinished
        WHERE trxFinished.account.idKeycloak IN :userIds
        GROUP BY trxFinished.account.idKeycloak
    """)
    fun sumUpForUsers(userIds: Set<String>): List<Pair<String, BigDecimal>>

}
