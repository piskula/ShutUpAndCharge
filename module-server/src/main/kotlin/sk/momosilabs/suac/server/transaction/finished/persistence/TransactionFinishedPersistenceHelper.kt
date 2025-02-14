package sk.momosilabs.suac.server.transaction.finished.persistence

import com.querydsl.core.types.dsl.BooleanExpression
import sk.momosilabs.suac.server.transaction.finished.model.TransactionFinishedFilter
import sk.momosilabs.suac.server.transaction.finished.persistence.entity.QChargingFinishedEntity
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset

fun TransactionFinishedFilter.transformToWhereClause(
    qTransaction: QChargingFinishedEntity,
): BooleanExpression? {
    val expressions = mutableListOf<BooleanExpression>()

    if (timeFrom != null)
        expressions.add(qTransaction.timeStartUtc.after(LocalDateTime.ofInstant(timeFrom, ZoneOffset.UTC)))

    if (timeTo != null)
        expressions.add(qTransaction.timeStartUtc.before(LocalDateTime.ofInstant(timeTo, ZoneOffset.UTC)))

    if (kwhFrom != null)
        expressions.add(qTransaction.kwh.goe(kwhFrom))

    if (kwhTo != null)
        expressions.add(qTransaction.kwh.loe(kwhTo))

    if (priceFrom != null)
        expressions.add(qTransaction.price.abs().goe(priceFrom))

    if (priceTo != null)
        expressions.add(qTransaction.price.abs().loe(priceTo))

    if (onlyCredit != null)
        expressions.add(if (onlyCredit) qTransaction.price.gt(BigDecimal.ZERO) else qTransaction.price.lt(BigDecimal.ZERO))

    if (chargingStationIds.isNotEmpty())
        expressions.add(qTransaction.stationId.`in`(chargingStationIds))

    if (accountIds.isNotEmpty())
        expressions.add(qTransaction.account.id.`in`(accountIds))

    return expressions.joinWithAnd()
}

fun Collection<BooleanExpression>.joinWithAnd() =
    if (isEmpty()) null else reduce { f, s -> f.and(s) }
