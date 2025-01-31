package sk.momosilabs.suac.api.transaction.temporary.dto

import java.time.OffsetDateTime

data class TransactionTemporaryDTO(
    val id: Long,
    val trxNumber: Int,
    val trxIdentifier: String,
    val energyMeter: Long,
    val timeStart: OffsetDateTime,
    val accountId: Long,
    val accountName: String,
)
