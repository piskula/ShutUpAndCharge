package org.momosi.shutupandcharge.core.transaction

import io.ktor.client.statement.bodyAsBytes
import io.ktor.http.ContentDisposition
import io.ktor.http.HttpHeaders
import org.momosi.shutupandcharge.core.common.coroutines.runCatchingCancellable
import org.momosi.shutupandcharge.core.common.logger.Logger
import org.momosi.shutupandcharge.core.common.model.Page
import org.momosi.shutupandcharge.core.common.network.onNetworkFailure
import org.momosi.shutupandcharge.core.common.network.onNoConnectionFailure
import org.momosi.shutupandcharge.core.network.dto.toPage
import org.momosi.shutupandcharge.core.transaction.api.TransactionApi
import org.momosi.shutupandcharge.core.transaction.dto.toDto
import org.momosi.shutupandcharge.core.transaction.dto.toMonthlyKwhChart
import org.momosi.shutupandcharge.core.transaction.dto.toMonthlySummary
import org.momosi.shutupandcharge.core.transaction.dto.toPendingTransaction
import org.momosi.shutupandcharge.core.transaction.dto.toTransaction
import org.momosi.shutupandcharge.core.transaction.model.ExportedFile
import org.momosi.shutupandcharge.core.transaction.model.MonthlyKwhChart
import org.momosi.shutupandcharge.core.transaction.model.MonthlySummary
import org.momosi.shutupandcharge.core.transaction.model.PendingTransaction
import org.momosi.shutupandcharge.core.transaction.model.Transaction
import org.momosi.shutupandcharge.core.transaction.model.TransactionFilter

private const val TAG = "TransactionRepository"
private const val TRANSACTION_SORT = "timeStartUtc,desc"
private const val MONTHLY_SORT = "month,desc"
private const val EXPORT_FALLBACK_FILE_NAME = "transactions.xlsx"
private const val EXPORT_MIME_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"

internal class TransactionRepositoryImpl(private val api: TransactionApi) : TransactionRepository {

    override suspend fun getTransactions(
        filter: TransactionFilter,
        page: Int,
        size: Int,
    ): Result<Page<Transaction>> = runCatchingCancellable {
        api.getFinished(filter.toDto(), page, size, TRANSACTION_SORT).toPage { it.toTransaction() }
    }.logFailures("load transactions")

    override suspend fun exportTransactions(filter: TransactionFilter): Result<ExportedFile> = runCatchingCancellable {
        val response = api.exportFinished(filter.toDto(), TRANSACTION_SORT)
        val fileName = response.headers[HttpHeaders.ContentDisposition]
            ?.let { runCatching { ContentDisposition.parse(it).parameter(ContentDisposition.Parameters.FileName) }.getOrNull() }
            ?.takeIf { it.isNotBlank() }
            ?: EXPORT_FALLBACK_FILE_NAME
        ExportedFile(bytes = response.bodyAsBytes(), fileName = fileName, mimeType = EXPORT_MIME_TYPE)
    }.logFailures("export transactions")

    override suspend fun getMonthlyKwh(filter: TransactionFilter): Result<MonthlyKwhChart> = runCatchingCancellable {
        api.getMonthlyKwh(filter.toDto()).toMonthlyKwhChart()
    }.logFailures("load monthly kWh chart")

    override suspend fun getLastChargings(page: Int, size: Int): Result<Page<Transaction>> = runCatchingCancellable {
        api.getLastChargings(page, size, TRANSACTION_SORT).toPage { it.toTransaction() }
    }.logFailures("load last chargings")

    override suspend fun getMonthlySummary(page: Int, size: Int): Result<Page<MonthlySummary>> = runCatchingCancellable {
        api.getMonthlySummary(page, size, MONTHLY_SORT).toPage { it.toMonthlySummary() }
    }.logFailures("load monthly summary")

    override suspend fun getPendingTransactions(page: Int, size: Int): Result<Page<PendingTransaction>> = runCatchingCancellable {
        api.getPending(page, size, null).toPage { it.toPendingTransaction() }
    }.logFailures("load pending transactions")

    override suspend fun syncPendingTransactions(): Result<Unit> = runCatchingCancellable {
        api.syncPending()
    }.logFailures("sync pending transactions")

    private fun <T> Result<T>.logFailures(action: String): Result<T> = this
        .onNoConnectionFailure { Logger.w(TAG, it, "Failed to $action (offline)") }
        .onNetworkFailure { Logger.e(TAG, it, "Failed to $action") }
}
