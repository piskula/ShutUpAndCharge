package org.momosi.shutupandcharge.core.transaction.api

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.statement.HttpResponse
import org.momosi.shutupandcharge.dto.MonthlyKwhChartDto
import org.momosi.shutupandcharge.dto.MonthlyTransactionSummaryDto
import org.momosi.shutupandcharge.dto.PageDto
import org.momosi.shutupandcharge.dto.TransactionFilterDto
import org.momosi.shutupandcharge.dto.TransactionFinishedDto
import org.momosi.shutupandcharge.dto.TransactionTemporaryDto

interface TransactionApi {

    @POST("api/transaction/finished")
    suspend fun getFinished(
        @Body filter: TransactionFilterDto,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String?,
    ): PageDto<TransactionFinishedDto>

    @Headers("Accept: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @POST("api/transaction/finished/export")
    suspend fun exportFinished(@Body filter: TransactionFilterDto, @Query("sort") sort: String?): HttpResponse

    @POST("api/transaction/finished/monthlyKwh")
    suspend fun getMonthlyKwh(@Body filter: TransactionFilterDto): MonthlyKwhChartDto

    @GET("api/dashboard/lastTransactions")
    suspend fun getLastChargings(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String?,
    ): PageDto<TransactionFinishedDto>

    @GET("api/dashboard/monthlySummary")
    suspend fun getMonthlySummary(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String?,
    ): PageDto<MonthlyTransactionSummaryDto>

    @POST("api/transaction/temporary")
    suspend fun getPending(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String?,
    ): PageDto<TransactionTemporaryDto>

    @POST("api/transaction/temporary/sync")
    suspend fun syncPending()
}
