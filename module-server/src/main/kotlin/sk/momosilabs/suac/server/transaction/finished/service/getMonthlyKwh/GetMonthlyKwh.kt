package sk.momosilabs.suac.server.transaction.finished.service.getMonthlyKwh

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sk.momosilabs.suac.server.common.IsUser
import sk.momosilabs.suac.server.security.service.CurrentUserService
import sk.momosilabs.suac.server.transaction.finished.model.KwhRecord
import sk.momosilabs.suac.server.transaction.finished.model.MonthlyKwhChart
import sk.momosilabs.suac.server.transaction.finished.model.MonthlyKwhSeries
import sk.momosilabs.suac.server.transaction.finished.model.TransactionFinishedFilter
import sk.momosilabs.suac.server.transaction.finished.persistence.TransactionFinishedPersistence
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZoneOffset

private val ZONE: ZoneId = ZoneId.of("Europe/Bratislava")
private const val MAX_MONTHS = 12L
private const val MAX_USERS = 5
private const val KWH_SCALE = 3
private const val OTHER_NAME = "Other"

@Service
open class GetMonthlyKwh(
    private val currentUserService: CurrentUserService,
    private val transactionPersistence: TransactionFinishedPersistence,
): GetMonthlyKwhUseCase {

    @IsUser
    @Transactional(readOnly = true)
    override fun get(filter: TransactionFinishedFilter): MonthlyKwhChart {
        val scopedFilter = if (currentUserService.isAdmin())
            filter
        else
            filter.copy(accountIds = setOf(currentUserService.keycloakId()))

        val endMonth = scopedFilter.timeTo
            ?.let { YearMonth.from(it.atZone(ZONE)) }
            ?: YearMonth.now(ZONE)
        var startMonth = endMonth.minusMonths(MAX_MONTHS - 1)
        val filterStartMonth = scopedFilter.timeFrom?.let { YearMonth.from(it.atZone(ZONE)) }
        if (filterStartMonth != null && filterStartMonth.isAfter(startMonth))
            startMonth = filterStartMonth

        if (startMonth.isAfter(endMonth))
            return MonthlyKwhChart(months = emptyList(), series = emptyList())

        val months = generateSequence(startMonth) { it.plusMonths(1) }
            .takeWhile { !it.isAfter(endMonth) }
            .toList()

        val records = transactionPersistence.getKwhRecords(
            filter = scopedFilter,
            fromUtc = startMonth.atDay(1).toUtcStartOfDay(),
            toUtcExclusive = endMonth.plusMonths(1).atDay(1).toUtcStartOfDay(),
        )

        return MonthlyKwhChart(
            months = months,
            series = buildSeries(months, records),
        )
    }

    private fun LocalDate.toUtcStartOfDay(): LocalDateTime =
        atStartOfDay(ZONE).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()

    private class UserKwh(
        val accountId: Long,
        val name: String,
        val kwh: List<BigDecimal>,
    ) {
        val total: BigDecimal = kwh.fold(BigDecimal.ZERO) { sum, value -> sum.add(value) }
    }

    private fun buildSeries(months: List<YearMonth>, records: List<KwhRecord>): List<MonthlyKwhSeries> {
        val monthIndex: Map<YearMonth, Int> = months.withIndex().associate { it.value to it.index }

        val users = records
            .groupBy { it.accountId }
            .map { entry ->
                val values = MutableList(months.size) { BigDecimal.ZERO }
                entry.value.forEach { record ->
                    val month = YearMonth.from(record.timeStartUtc.atZone(ZoneOffset.UTC).withZoneSameInstant(ZONE))
                    val index = monthIndex[month]
                    if (index != null)
                        values[index] = values[index].add(record.kwh)
                }
                UserKwh(entry.key, entry.value.first().accountName, values)
            }
            .filter { it.total.signum() > 0 }
            .sortedWith(
                compareByDescending<UserKwh> { it.total }
                    .thenBy { it.name }
                    .thenBy { it.accountId }
            )

        val top = users.take(MAX_USERS)
        val rest = users.drop(MAX_USERS)

        val series = top.map { user ->
            MonthlyKwhSeries(
                accountId = user.accountId,
                name = user.name,
                other = false,
                kwh = user.kwh.map { it.toKwhScale() },
            )
        }.toMutableList()

        if (rest.isNotEmpty()) {
            series.add(
                MonthlyKwhSeries(
                    accountId = null,
                    name = OTHER_NAME,
                    other = true,
                    kwh = months.indices.map { index ->
                        rest.fold(BigDecimal.ZERO) { sum, user -> sum.add(user.kwh[index]) }.toKwhScale()
                    },
                )
            )
        }

        return series
    }

    private fun BigDecimal.toKwhScale(): BigDecimal = setScale(KWH_SCALE, RoundingMode.HALF_UP)

}
