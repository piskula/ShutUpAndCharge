package sk.momosilabs.suac.server.transaction.finished.persistence

import com.querydsl.core.QueryResults
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.CaseBuilder
import com.querydsl.jpa.JPQLQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import sk.momosilabs.suac.server.account.persistence.repository.AccountRepository
import sk.momosilabs.suac.server.transaction.finished.model.ChargingToCreate
import sk.momosilabs.suac.server.transaction.finished.model.MonthlyTransactionSummary
import sk.momosilabs.suac.server.transaction.finished.model.TransactionFinished
import sk.momosilabs.suac.server.transaction.finished.model.TransactionFinishedFilter
import sk.momosilabs.suac.server.transaction.finished.persistence.entity.ChargingFinishedEntity
import sk.momosilabs.suac.server.transaction.finished.persistence.entity.QChargingFinishedEntity
import sk.momosilabs.suac.server.transaction.finished.persistence.repository.ChargingFinishedRepository
import java.math.BigDecimal
import java.time.LocalDate

@Repository
open class TransactionFinishedPersistenceProvider(
    private val accountRepository: AccountRepository,
    private val chargingRepository: ChargingFinishedRepository,
    private val entityManager: EntityManager,
): TransactionFinishedPersistence {

    private val transaction: QChargingFinishedEntity = QChargingFinishedEntity.chargingFinishedEntity

    private val year = transaction.timeStartUtc.year()
    private val month = transaction.timeStartUtc.month()
    private val negativeCount = CaseBuilder()
        .`when`(transaction.price.lt(BigDecimal.ZERO)).then(1L).otherwise(0L).sum()
    private val positiveCount = CaseBuilder()
        .`when`(transaction.price.gt(BigDecimal.ZERO)).then(1L).otherwise(0L).sum()
    private val negativeSum = CaseBuilder()
        .`when`(transaction.price.lt(BigDecimal.ZERO)).then(transaction.price).otherwise(BigDecimal.ZERO).sum()
    private val positiveSum = CaseBuilder()
        .`when`(transaction.price.gt(BigDecimal.ZERO)).then(transaction.price).otherwise(BigDecimal.ZERO).sum()
    private val totalSum = transaction.price.sum()

    @Transactional(readOnly = true)
    override fun getAll(filter: TransactionFinishedFilter, pageable: Pageable): Page<TransactionFinished> =
        JPAQueryFactory(entityManager)
            .select(transaction)
            .from(transaction)
            .where(filter.transformToWhereClause(transaction))
            .applyPagination(pageable)
            .fetchResults()
            .toPageResult(pageable, ChargingFinishedEntity::toModel)

    @Transactional(readOnly = true)
    override fun getNegativeByUserId(userId: String, pageable: Pageable): Page<TransactionFinished> =
        chargingRepository.findAllByAccountIdKeycloakAndPriceLessThan(userId, BigDecimal.ZERO, pageable)
            .map { it.toModel() }

    @Transactional(readOnly = true)
    override fun sumUpForUsers(userIds: Set<String>): Map<String, BigDecimal> =
        chargingRepository.sumUpForUsers(userIds).toMap()

    @Transactional
    override fun saveFinishedCharging(charging: ChargingToCreate): TransactionFinished {
        val account = accountRepository.getReferenceById(charging.userId)
        val chargingEntity = charging.asNewEntity { account }
        return chargingRepository.save(chargingEntity).toModel()
    }

    @Transactional
    override fun saveFinishedChargingBulk(chargings: Collection<ChargingToCreate>): Int {
        val accountIds = chargings.mapTo(HashSet()) { it.userId }
        val accounts = accountRepository.findAllById(accountIds).associateBy { it.id }

        return chargingRepository.saveAll(
            chargings.map { it.asNewEntity { accountId -> accounts[accountId]!! } }
        ).size
    }

    private fun <T> JPQLQuery<T>.applyPagination(pageable: Pageable) =
        offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(*pageable.sort.toQueryDslOrderBy())

    private fun <T, U : Any> QueryResults<T>.toPageResult(pageable: Pageable, mapper: (T) -> U): Page<U> = PageImpl(
        results.map { mapper.invoke(it) },
        pageable,
        total,
    )

    @Transactional(readOnly = true)
    override fun getMonthlySummaryByUserId(userId: String, pageable: Pageable): Page<MonthlyTransactionSummary> {
        val baseQuery = JPAQueryFactory(entityManager)
            .from(transaction)
            .where(transaction.account.idKeycloak.eq(userId))

        val content = baseQuery.clone()
            .select(year, month, negativeCount, positiveCount, negativeSum, positiveSum, totalSum)
            .groupBy(year, month)
            .applyPagination(pageable)
            .fetch()
            .map { tuple ->
                val year = tuple.get(year)!!
                val month = tuple.get(month)!!
                MonthlyTransactionSummary(
                    year = year,
                    month = month,
                    monthStart = LocalDate.of(year, month, 1),
                    negativeCount = tuple.get(negativeCount) ?: 0L,
                    positiveCount = tuple.get(positiveCount) ?: 0L,
                    negativeSum = tuple.get(negativeSum) ?: BigDecimal.ZERO,
                    positiveSum = tuple.get(positiveSum) ?: BigDecimal.ZERO,
                    totalSum = tuple.get(totalSum) ?: BigDecimal.ZERO,
                )
            }

        val total = baseQuery.clone()
            .select(year, month)
            .groupBy(year, month)
            .fetch()
            .size.toLong()

        return PageImpl(content, pageable, total)
    }

    private fun Sort.toQueryDslOrderBy(): Array<OrderSpecifier<*>> {
        val orders = if (isSorted) toList() else listOf(Sort.Order.desc("id"))
        return orders.flatMap { orderBy ->
            val orderDir = if (orderBy.isAscending) Order.ASC else Order.DESC
            val fields = when (orderBy.property) {
                "timeStartUtc" -> listOf(transaction.timeStartUtc)
                "account.lastName" -> listOf(transaction.account.lastName, transaction.account.firstName)
                "stationId" -> listOf(transaction.stationId)
                "kwh" -> listOf(transaction.kwh)
                "price" -> listOf(transaction.price)
                // aggregation functions:
                "month" -> listOf(year, month)
                "negativeCount" -> listOf(negativeCount)
                "positiveCount" -> listOf(positiveCount)
                "negativeSum" -> listOf(negativeSum)
                "positiveSum" -> listOf(positiveSum)
                "totalSum" -> listOf(totalSum)
                else -> listOf(transaction.id)
            }

            return@flatMap fields.map { OrderSpecifier(orderDir, it) }
        }.toTypedArray()
    }

}
