package org.momosi.shutupandcharge.core.account

import org.momosi.shutupandcharge.core.account.api.AccountApi
import org.momosi.shutupandcharge.core.account.dto.toAccount
import org.momosi.shutupandcharge.core.account.model.Account
import org.momosi.shutupandcharge.core.common.coroutines.runCatchingCancellable
import org.momosi.shutupandcharge.core.common.logger.Logger
import org.momosi.shutupandcharge.core.common.model.Page
import org.momosi.shutupandcharge.core.common.network.onNetworkFailure
import org.momosi.shutupandcharge.core.common.network.onNoConnectionFailure
import org.momosi.shutupandcharge.core.network.dto.toPage

private const val TAG = "AccountRepository"

internal class AccountRepositoryImpl(private val api: AccountApi) : AccountRepository {

    override suspend fun getMyBalance(): Result<Double> = runCatchingCancellable {
        api.getMyBalance()
    }.logFailures("load balance")

    override suspend fun getAccounts(page: Int, size: Int): Result<Page<Account>> = runCatchingCancellable {
        api.getAccounts(page, size, null).toPage { it.toAccount() }
    }.logFailures("load accounts")

    override suspend fun setVerifiedForCharging(accountId: Long, verified: Boolean): Result<Boolean> = runCatchingCancellable {
        api.updateVerifiedFlag(accountId, verified)
    }.logFailures("update verified flag")

    override suspend fun topUp(accountId: Long, amount: Double): Result<Double> = runCatchingCancellable {
        api.topUp(accountId, amount)
    }.logFailures("top up account")

    override suspend fun setChipUid(accountId: Long, chipUid: String?): Result<Unit> = runCatchingCancellable {
        api.updateChipUid(accountId, chipUid?.trim().orEmpty())
    }.logFailures("update chip UID")

    private fun <T> Result<T>.logFailures(action: String): Result<T> = this
        .onNoConnectionFailure { Logger.w(TAG, it, "Failed to $action (offline)") }
        .onNetworkFailure { Logger.e(TAG, it, "Failed to $action") }
}
