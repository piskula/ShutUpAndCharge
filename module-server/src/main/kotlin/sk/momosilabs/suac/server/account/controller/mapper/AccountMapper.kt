package sk.momosilabs.suac.server.account.controller.mapper

import sk.momosilabs.suac.api.account.dto.AccountDTO
import sk.momosilabs.suac.server.account.model.Account

fun Account.toDto() = AccountDTO(
    id = id,
    idKeycloak = idKeycloak,
    firstName = firstName,
    lastName = lastName,
    verifiedForCharging = verifiedForCharging,
    assignedChipUid = assignedChipUid,
)
