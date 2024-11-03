package sk.momosilabs.suac.server.charging.persistence

import sk.momosilabs.suac.server.account.entity.AccountEntity
import sk.momosilabs.suac.server.charging.model.ChargingListItem
import sk.momosilabs.suac.server.charging.persistence.entity.ChargingFinishedEntity
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

fun ChargingFinishedEntity.toModel() = ChargingListItem(
    id = id,
    guid = guid,
    time = time,
    kwh = kwh,
    price = kwh.multiply(BigDecimal.valueOf(29L, 2)).round(MathContext(2, RoundingMode.HALF_UP)),
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
