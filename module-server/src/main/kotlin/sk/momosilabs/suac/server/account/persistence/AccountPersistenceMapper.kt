package sk.momosilabs.suac.server.account.persistence

import sk.momosilabs.suac.server.account.entity.AccountEntity
import sk.momosilabs.suac.server.account.model.Account
import java.math.BigDecimal

fun AccountEntity.toModel() = Account(
    id = id,
    idKeycloak = idKeycloak,
    firstName = firstName,
    lastName = lastName,
    balance = BigDecimal.ZERO,
    verifiedForCharging = verifiedForCharging,
    assignedChipUid = assignedChipUid,
)
