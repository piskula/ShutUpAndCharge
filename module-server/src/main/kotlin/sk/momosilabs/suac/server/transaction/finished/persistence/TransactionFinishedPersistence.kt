package sk.momosilabs.suac.server.transaction.finished.persistence

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import sk.momosilabs.suac.server.transaction.finished.model.TransactionFinished
import sk.momosilabs.suac.server.transaction.finished.model.ChargingToCreate
import sk.momosilabs.suac.server.transaction.finished.model.TransactionFinishedFilter
import java.math.BigDecimal

interface TransactionFinishedPersistence {

    fun getAll(filter: TransactionFinishedFilter, pageable: Pageable): Page<TransactionFinished>

    fun getNegativeByUserId(userId: Long, pageable: Pageable): Page<TransactionFinished>

    fun sumUpForUsers(userIds: Set<Long>): Map<Long, BigDecimal>

    fun saveFinishedCharging(charging: ChargingToCreate): TransactionFinished

    fun saveFinishedChargingBulk(chargings: Collection<ChargingToCreate>): Int

}
