package sk.momosilabs.suac.server.account.model

import java.math.BigDecimal

data class Account(
    val id: Long,
    val idKeycloak: String,
    val firstName: String,
    val lastName: String,
    var balance: BigDecimal,
    val verifiedForCharging: Boolean,
    val assignedChipUid: String?,
)
