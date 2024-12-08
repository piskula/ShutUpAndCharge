package sk.momosilabs.suac.server.transaction.controller.mapper

import sk.momosilabs.suac.api.transaction.ChargingListDTO
import sk.momosilabs.suac.api.transaction.TransactionFilterDTO
import sk.momosilabs.suac.server.transaction.model.ChargingListItem
import sk.momosilabs.suac.server.transaction.model.TransactionFilter
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

fun TransactionFilterDTO.toModel() = TransactionFilter(
    timeFrom = timeFrom?.toInstant(),
    timeTo = timeTo?.toInstant(),
    kwhFrom = kwhTo,
    kwhTo = kwhTo,
    priceFrom = priceFrom,
    priceTo = priceTo,
    onlyCredit = onlyCredit,
    chargingStationIds = chargingStationIds,
    accountIds = accountIds,
)
