package sk.momosilabs.suac.server.transaction.persistence

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import sk.momosilabs.suac.server.transaction.model.ChargingListItem
import sk.momosilabs.suac.server.transaction.model.ChargingToCreate

interface TransactionFinishedPersistence {

    fun getAll(filterByUserId: Long?, pageable: Pageable): Page<ChargingListItem>

    fun getNegativeByUserId(userId: Long, pageable: Pageable): Page<ChargingListItem>

    fun saveFinishedCharging(charging: ChargingToCreate, userId: Long): ChargingListItem

}
