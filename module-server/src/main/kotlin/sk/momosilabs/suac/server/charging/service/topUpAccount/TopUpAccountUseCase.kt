package sk.momosilabs.suac.server.charging.service.topUpAccount

import java.math.BigDecimal

interface TopUpAccountUseCase {

    fun topUp(accountId: Long, amount: BigDecimal): BigDecimal

}
