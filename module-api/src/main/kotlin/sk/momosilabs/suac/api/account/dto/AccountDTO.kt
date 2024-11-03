package sk.momosilabs.suac.api.account.dto

data class AccountDTO(
    val id: Long,
    val idKeycloak: String,
    val firstName: String,
    val lastName: String,
    val verifiedForCharging: Boolean,
)
