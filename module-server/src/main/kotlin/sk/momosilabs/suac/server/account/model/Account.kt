package sk.momosilabs.suac.server.account.model

data class Account(
    val id: Long,
    val idKeycloak: String,
    val firstName: String,
    val lastName: String,
    val verifiedForCharging: Boolean,
    val assignedChipUid: String?,
)
