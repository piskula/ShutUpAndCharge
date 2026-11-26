package org.momosi.shutupandcharge.core.account

import org.momosi.shutupandcharge.core.account.model.Account
import org.momosi.shutupandcharge.core.common.model.Page

interface AccountRepository {

    suspend fun getMyBalance(): Result<Double>

    suspend fun getAccounts(page: Int, size: Int): Result<Page<Account>>

    suspend fun setVerifiedForCharging(accountId: Long, verified: Boolean): Result<Boolean>

    suspend fun topUp(accountId: Long, amount: Double): Result<Double>

    suspend fun setChipUid(accountId: Long, chipUid: String?): Result<Unit>
}
