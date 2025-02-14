package sk.momosilabs.suac.server.transaction.temporary.model

data class TransactionTemporaryToMatch(
    val trxIdentifier: String,
    val energyMeter: Long,
    val accountId: Long,
)
