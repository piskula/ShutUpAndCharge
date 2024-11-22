package sk.momosilabs.suac.server.transaction.persistence.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sk.momosilabs.suac.server.transaction.persistence.entity.ChargingFinishedEntity
import java.math.BigDecimal

@Repository
interface ChargingFinishedRepository: JpaRepository<ChargingFinishedEntity, Long> {

    @Query("SELECT e FROM #{#entityName} e WHERE :userId IS NULL OR :userId = account.id")
    fun findAllByAccountId(userId: Long?, pageable: Pageable): Page<ChargingFinishedEntity>

    fun findAllByAccountIdAndPriceLessThan(userId: Long, lessThan: BigDecimal, pageable: Pageable): Page<ChargingFinishedEntity>

    fun countByAccountId(userId: Long): Long

}
