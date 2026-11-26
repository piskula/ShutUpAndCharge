package org.momosi.shutupandcharge.core.account.model

data class Account(val id: Long, val keycloakId: String, val firstName: String, val lastName: String, val balance: Double, val verifiedForCharging: Boolean, val chipUid: String?) {
    val fullName: String get() = listOf(firstName, lastName).filter { it.isNotBlank() }.joinToString(" ")
}
