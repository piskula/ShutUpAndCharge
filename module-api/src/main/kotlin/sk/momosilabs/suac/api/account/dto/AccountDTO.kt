package sk.momosilabs.suac.api.account.dto

import java.math.BigDecimal

data class AccountDTO(
    val id: Long,
    val idKeycloak: String,
    val firstName: String,
    val lastName: String,
    val balance: BigDecimal,
    val verifiedForCharging: Boolean,
    val assignedChipUid: String?,
)
