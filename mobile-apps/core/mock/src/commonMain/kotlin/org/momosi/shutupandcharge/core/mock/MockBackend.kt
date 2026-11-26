package org.momosi.shutupandcharge.core.mock

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.momosi.shutupandcharge.core.account.model.Account
import org.momosi.shutupandcharge.core.common.network.ApiException
import org.momosi.shutupandcharge.core.transaction.model.PendingTransaction
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.Instant

private const val HTTP_NOT_FOUND = 404

/** In-memory "server" shared by all mock repositories so that actions change every screen consistently. */
internal class MockBackend(private val clock: Clock = Clock.System) {

    private val mutex = Mutex()
    private val random = Random(clock.now().toEpochMilliseconds())
    private var nextGuid = 0

    private val accounts: MutableList<Account> = MockData.accounts().toMutableList()
    private val transactions: MutableList<MockTransaction> = MockData.transactions(accounts, clock.now()).toMutableList()
    private val pending: MutableList<PendingTransaction> = MockData.pending(accounts, clock.now()).toMutableList()

    fun now(): Instant = clock.now()

    suspend fun accounts(): List<Account> = mutex.withLock { accounts.sortedBy { it.id } }

    suspend fun account(accountId: Long): Account = mutex.withLock { findAccount(accountId) }

    suspend fun transactions(): List<MockTransaction> = mutex.withLock { transactions.sortedByDescending { it.transaction.time } }

    suspend fun pending(): List<PendingTransaction> = mutex.withLock { pending.sortedByDescending { it.startedAt } }

    suspend fun setVerified(accountId: Long, verified: Boolean): Boolean = mutex.withLock {
        updateAccount(accountId) { it.copy(verifiedForCharging = verified) }.verifiedForCharging
    }

    suspend fun setChipUid(accountId: Long, chipUid: String?) {
        mutex.withLock { updateAccount(accountId) { it.copy(chipUid = chipUid) } }
    }

    suspend fun topUp(accountId: Long, amount: Double): Double = mutex.withLock {
        val account = updateAccount(accountId) { it.copy(balance = roundCents(it.balance + amount)) }
        transactions += MockData.topUp(account, amount, clock.now(), newGuid())
        account.balance
    }

    suspend fun recordCharging(
        accountId: Long,
        kwh: Double,
        byChip: Boolean = false,
    ) {
        mutex.withLock { addCharging(accountId, kwh, byChip) }
    }

    suspend fun syncPending() {
        mutex.withLock {
            pending.forEach { item ->
                val account = accounts.firstOrNull { it.fullName == item.accountName } ?: return@forEach
                addCharging(account.id, kwh = roundCents(random.nextDouble(6.0, 22.0)), byChip = item.identifier != "app")
            }
            pending.clear()
        }
    }

    private fun addCharging(
        accountId: Long,
        kwh: Double,
        byChip: Boolean,
    ) {
        val charging = MockData.charging(findAccount(accountId), kwh, clock.now(), newGuid(), byChip = byChip, withLink = true)
        updateAccount(accountId) { it.copy(balance = roundCents(it.balance + charging.transaction.price)) }
        transactions += charging
    }

    private fun findAccount(accountId: Long): Account = accounts.firstOrNull { it.id == accountId } ?: throw ApiException.HttpError(HTTP_NOT_FOUND, "Account $accountId not found")

    private fun updateAccount(accountId: Long, transform: (Account) -> Account): Account {
        val index = accounts.indexOfFirst { it.id == accountId }
        if (index < 0) throw ApiException.HttpError(HTTP_NOT_FOUND, "Account $accountId not found")
        return transform(accounts[index]).also { accounts[index] = it }
    }

    private fun newGuid(): String = "demo-new-${nextGuid++}"
}
