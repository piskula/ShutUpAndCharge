package sk.momosilabs.suac.server.transaction.finished.persistence.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sk.momosilabs.suac.server.transaction.finished.persistence.entity.ChargingFinishedEntity
import java.math.BigDecimal

@Repository
interface ChargingFinishedRepository: JpaRepository<ChargingFinishedEntity, Long> {

    fun findAllByAccountIdAndPriceLessThan(userId: Long, lessThan: BigDecimal, pageable: Pageable): Page<ChargingFinishedEntity>

    fun countByAccountId(userId: Long): Long

}
