package sk.momosilabs.suac.server.transaction.temporary.controller.mapper

import sk.momosilabs.suac.api.transaction.temporary.dto.TransactionTemporaryDTO
import sk.momosilabs.suac.server.transaction.temporary.model.TransactionTemporary

fun TransactionTemporary.toDto() = TransactionTemporaryDTO(
    id = id,
    trxNumber = trxNumber,
    trxIdentifier = trxIdentifier,
    energyMeter = energyMeter,
    timeStart = timeStart,
    accountId = accountId,
    accountName = accountName,
)
