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

    fun findAllByAccountIdAndPriceLessThan(userId: Long, lessThan: BigDecimal, pageable: Pageable): Page<ChargingFinishedEntity>

    fun countByAccountId(userId: Long): Long

    @Query("""
        SELECT new kotlin.Pair(
            trxFinished.account.id,
            COALESCE(SUM(trxFinished.price), 0)
        )
        FROM #{#entityName} trxFinished
        WHERE trxFinished.account.id IN :userIds
        GROUP BY trxFinished.account.id
    """)
    fun sumUpForUsers(userIds: Set<Long>): List<Pair<Long, BigDecimal>>

}
