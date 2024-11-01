package sk.momosilabs.suac.server.charging.persistence

import sk.momosilabs.suac.server.account.entity.AccountEntity
import sk.momosilabs.suac.server.charging.model.ChargingListItem
import sk.momosilabs.suac.server.charging.persistence.entity.ChargingFinishedEntity

fun ChargingFinishedEntity.toModel() = ChargingListItem(
    id = id,
    guid = guid,
    time = time,
    kwh = kwh,
    chargingStationId = stationId,
)

fun ChargingListItem.asNewEntity(account: AccountEntity) = ChargingFinishedEntity(
    id = 0L,
    guid = guid,
    account = account,
    time = time,
    kwh = kwh,
    stationId = chargingStationId,
)
