package sk.momosilabs.suac.server.charging.controller.mapper

import sk.momosilabs.suac.api.charging.ChargingListDTO
import sk.momosilabs.suac.server.charging.model.ChargingListItem
import java.time.ZoneOffset

fun ChargingListItem.toDto() = ChargingListDTO(
    guid = guid,
    time = time.atOffset(ZoneOffset.UTC),
    kwh = kwh,
    chargingStationId = chargingStationId,
)
