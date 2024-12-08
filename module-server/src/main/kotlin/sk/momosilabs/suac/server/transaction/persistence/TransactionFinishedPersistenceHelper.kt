package sk.momosilabs.suac.server.transaction.persistence

import com.querydsl.core.types.dsl.BooleanExpression
import sk.momosilabs.suac.server.transaction.model.TransactionFilter
import sk.momosilabs.suac.server.transaction.persistence.entity.QChargingFinishedEntity
import java.math.BigDecimal

fun TransactionFilter.transformToWhereClause(
    qTransaction: QChargingFinishedEntity,
): BooleanExpression? {
    val expressions = mutableListOf<BooleanExpression>()

    if (timeFrom != null)
        expressions.add(qTransaction.time.after(timeFrom))

    if (timeTo != null)
        expressions.add(qTransaction.time.before(timeTo))

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
