package sk.momosilabs.suac.server.transaction.controller.mapper

import sk.momosilabs.suac.api.transaction.ChargingListDTO
import sk.momosilabs.suac.server.transaction.model.ChargingListItem
import java.time.ZoneOffset

fun ChargingListItem.toDto() = ChargingListDTO(
    guid = guid,
    time = time.atOffset(ZoneOffset.UTC),
    kwh = kwh,
    price = price,
    chargingStationId = chargingStationId,
    accountId = accountId,
    accountName = accountName,
)
