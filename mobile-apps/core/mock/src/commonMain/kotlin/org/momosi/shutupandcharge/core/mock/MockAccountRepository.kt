package org.momosi.shutupandcharge.core.mock

import kotlinx.coroutines.delay
import org.momosi.shutupandcharge.core.account.AccountRepository
import org.momosi.shutupandcharge.core.account.model.Account
import org.momosi.shutupandcharge.core.common.coroutines.runCatchingCancellable
import org.momosi.shutupandcharge.core.common.model.Page

internal class MockAccountRepository(private val backend: MockBackend) : AccountRepository {

    override suspend fun getMyBalance(): Result<Double> = mockCall { backend.account(DEMO_ACCOUNT_ID).balance }

    override suspend fun getAccounts(page: Int, size: Int): Result<Page<Account>> = mockCall { backend.accounts().toPage(page, size) }

    override suspend fun setVerifiedForCharging(accountId: Long, verified: Boolean): Result<Boolean> = mockCall { backend.setVerified(accountId, verified) }

    override suspend fun topUp(accountId: Long, amount: Double): Result<Double> = mockCall { backend.topUp(accountId, amount) }

    override suspend fun setChipUid(accountId: Long, chipUid: String?): Result<Unit> = mockCall { backend.setChipUid(accountId, chipUid?.trim()?.ifBlank { null }) }
}

internal suspend inline fun <T> mockCall(block: () -> T): Result<T> {
    delay(MOCK_LATENCY)
    return runCatchingCancellable(block)
}
