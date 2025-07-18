package sk.momosilabs.suac.server.dashboard.service.getMyBalance

import java.math.BigDecimal

interface GetMyBalanceUseCase {

    fun getCurrentBalance(): BigDecimal

}
