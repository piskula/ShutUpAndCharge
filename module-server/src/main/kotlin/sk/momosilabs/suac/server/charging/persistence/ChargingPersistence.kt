package sk.momosilabs.suac.server.charging.persistence

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import sk.momosilabs.suac.server.charging.model.ChargingListItem

interface ChargingPersistence {

    fun getByUserId(userId: Long, pageable: Pageable): Page<ChargingListItem>

    fun saveFinishedCharging(charging: ChargingListItem, userId: Long): ChargingListItem

}
