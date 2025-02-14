package sk.momosilabs.suac.server.transaction.finished.persistence

import com.querydsl.core.QueryResults
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
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
import sk.momosilabs.suac.server.transaction.finished.model.TransactionFinished
import sk.momosilabs.suac.server.transaction.finished.model.ChargingToCreate
import sk.momosilabs.suac.server.transaction.finished.model.TransactionFinishedFilter
import sk.momosilabs.suac.server.transaction.finished.persistence.entity.ChargingFinishedEntity
import sk.momosilabs.suac.server.transaction.finished.persistence.entity.QChargingFinishedEntity
import sk.momosilabs.suac.server.transaction.finished.persistence.repository.ChargingFinishedRepository
import java.math.BigDecimal

@Repository
open class TransactionFinishedPersistenceProvider(
    private val accountRepository: AccountRepository,
    private val chargingRepository: ChargingFinishedRepository,
    private val entityManager: EntityManager,
): TransactionFinishedPersistence {

    val transaction: QChargingFinishedEntity = QChargingFinishedEntity.chargingFinishedEntity

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
    override fun getNegativeByUserId(userId: Long, pageable: Pageable): Page<TransactionFinished> =
        chargingRepository.findAllByAccountIdAndPriceLessThan(userId, BigDecimal.ZERO, pageable).map { it.toModel() }

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
            .orderBy(pageable.sort.toQueryDslOrderBy())

    private fun <T, U> QueryResults<T>.toPageResult(pageable: Pageable, mapper: (T) -> U): Page<U> = PageImpl(
        results.map { mapper.invoke(it) },
        pageable,
        total,
    )

    private fun Sort.toQueryDslOrderBy(): OrderSpecifier<*> {
        val orderBy = if (isSorted) this.get().findFirst().get() else Sort.Order.desc("id")
        val expression = when (orderBy.property) {
            "timeStartUtc" -> transaction.timeStartUtc
            "account.lastName" -> transaction.account.lastName
            "stationId" -> transaction.stationId
            "kwh" -> transaction.kwh
            "price" -> transaction.price
            else -> transaction.id
        }
        return OrderSpecifier(if (orderBy.isAscending) Order.ASC else Order.DESC, expression)
    }

}
