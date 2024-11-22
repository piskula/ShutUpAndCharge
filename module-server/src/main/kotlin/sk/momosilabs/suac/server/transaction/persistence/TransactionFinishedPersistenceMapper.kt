package sk.momosilabs.suac.server.transaction.persistence

import sk.momosilabs.suac.server.account.entity.AccountEntity
import sk.momosilabs.suac.server.transaction.model.ChargingListItem
import sk.momosilabs.suac.server.transaction.model.ChargingToCreate
import sk.momosilabs.suac.server.transaction.persistence.entity.ChargingFinishedEntity

fun ChargingFinishedEntity.toModel() = ChargingListItem(
    id = id,
    guid = guid,
    time = time,
    kwh = kwh,
    price = price,
    chargingStationId = stationId,
    accountId = account.id,
    accountName = "${account.firstName} ${account.lastName}",
)

fun ChargingToCreate.asNewEntity(account: AccountEntity) = ChargingFinishedEntity(
    id = 0L,
    guid = guid,
    account = account,
    time = time,
    kwh = kwh,
    stationId = chargingStationId,
    price = price,
)
