package org.momosi.shutupandcharge.core.mock

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.momosi.shutupandcharge.core.common.model.Page
import org.momosi.shutupandcharge.core.transaction.TransactionRepository
import org.momosi.shutupandcharge.core.transaction.model.ExportedFile
import org.momosi.shutupandcharge.core.transaction.model.MonthlyKwhChart
import org.momosi.shutupandcharge.core.transaction.model.MonthlyKwhSeries
import org.momosi.shutupandcharge.core.transaction.model.MonthlySummary
import org.momosi.shutupandcharge.core.transaction.model.PendingTransaction
import org.momosi.shutupandcharge.core.transaction.model.Transaction
import org.momosi.shutupandcharge.core.transaction.model.TransactionFilter
import org.momosi.shutupandcharge.core.transaction.model.TransactionType
import kotlin.time.Instant

private const val CHART_MONTHS = 6
private const val CHART_NAMED_SERIES = 3
private const val CHART_OTHER_NAME = "Other"
private const val EXPORT_FILE_NAME = "transactions-demo.csv"
private const val EXPORT_MIME_TYPE = "text/csv"

internal class MockTransactionRepository(private val backend: MockBackend, private val timeZone: TimeZone = TimeZone.currentSystemDefault()) : TransactionRepository {

    override suspend fun getTransactions(
        filter: TransactionFilter,
        page: Int,
        size: Int,
    ): Result<Page<Transaction>> = mockCall {
        filtered(filter).map { it.transaction }.toPage(page, size)
    }

    override suspend fun exportTransactions(filter: TransactionFilter): Result<ExportedFile> = mockCall {
        val rows = filtered(filter).map { (_, t) -> listOf(t.time.toString(), t.accountName, t.kwh.toString(), t.price.toString(), t.stationId.orEmpty()).joinToString(",") }
        val csv = (listOf("time,account,kwh,price,station") + rows).joinToString("\n")
        ExportedFile(bytes = csv.encodeToByteArray(), fileName = EXPORT_FILE_NAME, mimeType = EXPORT_MIME_TYPE)
    }

    override suspend fun getMonthlyKwh(filter: TransactionFilter): Result<MonthlyKwhChart> = mockCall {
        val months = lastMonths(backend.now())
        val perAccount = filtered(filter)
            .filterNot { it.transaction.isTopUp }
            .groupBy { it.transaction.accountName }
            .mapValues { (_, items) -> months.map { month -> items.filter { it.transaction.month() == month }.sumOf { it.transaction.kwh } } }
            .entries
            .sortedByDescending { (_, kwh) -> kwh.sum() }
        val named = perAccount.take(CHART_NAMED_SERIES).map { (name, kwh) -> MonthlyKwhSeries(name = name, isOther = false, kwh = kwh.map(::roundCents)) }
        val other = perAccount.drop(CHART_NAMED_SERIES)
            .takeIf { it.isNotEmpty() }
            ?.let { rest -> MonthlyKwhSeries(name = CHART_OTHER_NAME, isOther = true, kwh = months.indices.map { index -> roundCents(rest.sumOf { it.value[index] }) }) }
        MonthlyKwhChart(months = months.map { it.toMonthKey() }, series = named + listOfNotNull(other))
    }

    override suspend fun getLastChargings(page: Int, size: Int): Result<Page<Transaction>> = mockCall {
        backend.transactions().filter { it.accountId == DEMO_ACCOUNT_ID && !it.transaction.isTopUp }.map { it.transaction }.toPage(page, size)
    }

    override suspend fun getMonthlySummary(page: Int, size: Int): Result<Page<MonthlySummary>> = mockCall {
        backend.transactions()
            .filter { it.accountId == DEMO_ACCOUNT_ID }
            .map { it.transaction }
            .groupBy { it.month() }
            .entries
            .sortedByDescending { it.key }
            .map { (month, items) -> items.toMonthlySummary(month.atStartOfDayIn(timeZone)) }
            .toPage(page, size)
    }

    override suspend fun getPendingTransactions(page: Int, size: Int): Result<Page<PendingTransaction>> = mockCall { backend.pending().toPage(page, size) }

    override suspend fun syncPendingTransactions(): Result<Unit> = mockCall { backend.syncPending() }

    private suspend fun filtered(filter: TransactionFilter): List<MockTransaction> {
        val accountId = filter.onlyAccountKeycloakId?.let { keycloakId -> backend.accounts().firstOrNull { it.keycloakId == keycloakId }?.id }
        return backend.transactions().filter { item ->
            (filter.onlyAccountKeycloakId == null || item.accountId == accountId) &&
                when (filter.type) {
                    TransactionType.All -> true
                    TransactionType.TopUps -> item.transaction.isTopUp
                    TransactionType.Charging -> !item.transaction.isTopUp
                }
        }
    }

    private fun Transaction.month(): LocalDate = (time ?: backend.now()).monthStart()

    private fun Instant.monthStart(): LocalDate = toLocalDateTime(timeZone).date.let { LocalDate(it.year, it.month, 1) }

    private fun lastMonths(now: Instant): List<LocalDate> {
        val current = now.monthStart()
        return (CHART_MONTHS - 1 downTo 0).map { current.minus(it, DateTimeUnit.MONTH) }
    }
}

private fun LocalDate.toMonthKey(): String = "$year-${month.number.toString().padStart(2, '0')}"

private fun List<Transaction>.toMonthlySummary(monthStart: Instant): MonthlySummary {
    val (topUps, chargings) = partition { it.isTopUp }
    return MonthlySummary(
        monthStart = monthStart,
        chargingCount = chargings.size.toLong(),
        chargingSum = roundCents(chargings.sumOf { it.price }),
        topUpCount = topUps.size.toLong(),
        topUpSum = roundCents(topUps.sumOf { it.price }),
        total = roundCents(sumOf { it.price }),
    )
}
