package org.momosi.shutupandcharge.core.account.dto

import org.momosi.shutupandcharge.core.account.model.Account
import org.momosi.shutupandcharge.dto.AccountDto

internal fun AccountDto.toAccount(): Account = Account(
    id = id,
    keycloakId = idKeycloak,
    firstName = firstName,
    lastName = lastName,
    balance = balance,
    verifiedForCharging = verifiedForCharging,
    chipUid = assignedChipUid?.takeIf { it.isNotBlank() },
)
