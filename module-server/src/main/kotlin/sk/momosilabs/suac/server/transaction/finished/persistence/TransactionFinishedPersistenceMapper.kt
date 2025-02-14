package sk.momosilabs.suac.server.transaction.finished.persistence

import sk.momosilabs.suac.server.account.entity.AccountEntity
import sk.momosilabs.suac.server.transaction.finished.model.TransactionFinished
import sk.momosilabs.suac.server.transaction.finished.model.ChargingToCreate
import sk.momosilabs.suac.server.transaction.finished.persistence.entity.ChargingFinishedEntity
import java.time.LocalDateTime
import java.time.ZoneOffset

fun ChargingFinishedEntity.toModel() = TransactionFinished(
    id = id,
    guid = guid,
    time = timeStartUtc.toInstant(ZoneOffset.UTC),
    kwh = kwh,
    price = price,
    chargingStationId = stationId,
    accountId = account.id,
    accountName = "${account.firstName} ${account.lastName}",
)

fun ChargingToCreate.asNewEntity(accountResolver: (Long) -> AccountEntity) = ChargingFinishedEntity(
    id = 0L,
    guid = guid,
    account = accountResolver.invoke(userId),
    timeStartUtc = LocalDateTime.ofInstant(timeStart, ZoneOffset.UTC),
    kwh = kwh,
    stationId = stationId,
    price = price,
    stationSession = stationSession,
    energyMeter = energyMeter,
    triggeredByChipUid = chipUid,
    link = link,
)
