package org.momosi.shutupandcharge.core.account.api

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import org.momosi.shutupandcharge.dto.AccountDto
import org.momosi.shutupandcharge.dto.PageDto

interface AccountApi {

    @GET("api/dashboard/balance")
    suspend fun getMyBalance(): Double

    @GET("api/account")
    suspend fun getAccounts(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String?,
    ): PageDto<AccountDto>

    @POST("api/account/{accountId}/verified/{verified}")
    suspend fun updateVerifiedFlag(@Path("accountId") accountId: Long, @Path("verified") verified: Boolean): Boolean

    @POST("api/account/{accountId}/topUp")
    suspend fun topUp(@Path("accountId") accountId: Long, @Body amount: Double): Double

    @POST("api/account/{accountId}/chipUid")
    suspend fun updateChipUid(@Path("accountId") accountId: Long, @Body chipUid: String)
}
