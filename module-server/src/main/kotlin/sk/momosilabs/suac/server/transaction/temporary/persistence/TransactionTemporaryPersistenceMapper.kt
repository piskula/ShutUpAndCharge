package sk.momosilabs.suac.server.transaction.temporary.persistence

import sk.momosilabs.suac.server.transaction.temporary.model.TransactionTemporary
import sk.momosilabs.suac.server.transaction.temporary.persistence.entity.ChargingOngoingEntity
import java.time.ZoneOffset

fun ChargingOngoingEntity.toModel() = TransactionTemporary(
    id = id,
    trxNumber = trxNumber,
    trxIdentifier = trxIdentifier,
    energyMeter = energyMeter,
    timeStart = timeStartUtc.atOffset(ZoneOffset.UTC),
    accountId = account.id,
    accountName = "${account.firstName} ${account.lastName}",
)
