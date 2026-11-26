package org.momosi.shutupandcharge.dto

import kotlinx.serialization.Serializable

@Serializable
data class AccountDto(val id: Long, val idKeycloak: String, val firstName: String, val lastName: String, val balance: Double, val verifiedForCharging: Boolean, val assignedChipUid: String? = null)
