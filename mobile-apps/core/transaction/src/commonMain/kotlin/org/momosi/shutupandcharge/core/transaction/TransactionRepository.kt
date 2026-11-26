package org.momosi.shutupandcharge.core.transaction

import org.momosi.shutupandcharge.core.common.model.Page
import org.momosi.shutupandcharge.core.transaction.model.ExportedFile
import org.momosi.shutupandcharge.core.transaction.model.MonthlyKwhChart
import org.momosi.shutupandcharge.core.transaction.model.MonthlySummary
import org.momosi.shutupandcharge.core.transaction.model.PendingTransaction
import org.momosi.shutupandcharge.core.transaction.model.Transaction
import org.momosi.shutupandcharge.core.transaction.model.TransactionFilter

interface TransactionRepository {

    suspend fun getTransactions(
        filter: TransactionFilter,
        page: Int,
        size: Int,
    ): Result<Page<Transaction>>

    suspend fun exportTransactions(filter: TransactionFilter): Result<ExportedFile>

    suspend fun getMonthlyKwh(filter: TransactionFilter): Result<MonthlyKwhChart>

    suspend fun getLastChargings(page: Int, size: Int): Result<Page<Transaction>>

    suspend fun getMonthlySummary(page: Int, size: Int): Result<Page<MonthlySummary>>

    suspend fun getPendingTransactions(page: Int, size: Int): Result<Page<PendingTransaction>>

    suspend fun syncPendingTransactions(): Result<Unit>
}
