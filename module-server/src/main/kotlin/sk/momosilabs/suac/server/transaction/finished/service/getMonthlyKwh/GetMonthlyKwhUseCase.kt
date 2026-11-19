package sk.momosilabs.suac.server.transaction.finished.service.getMonthlyKwh

import sk.momosilabs.suac.server.transaction.finished.model.MonthlyKwhChart
import sk.momosilabs.suac.server.transaction.finished.model.TransactionFinishedFilter

interface GetMonthlyKwhUseCase {

    fun get(filter: TransactionFinishedFilter): MonthlyKwhChart

}
