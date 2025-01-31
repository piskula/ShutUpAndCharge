package sk.momosilabs.suac.server.transaction.temporary.model

import java.time.OffsetDateTime

data class TransactionTemporary(
    val id: Long,
    val trxNumber: Int,
    val trxIdentifier: String,
    val energyMeter: Long,
    val timeStart: OffsetDateTime,
    val accountId: Long,
    val accountName: String,
)
