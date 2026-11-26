package org.momosi.shutupandcharge.core.charger.api

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query
import org.momosi.shutupandcharge.dto.ChargerStatusDto

interface ChargerApi {

    @GET("info/chargerStatus")
    suspend fun getChargerStatus(): ChargerStatusDto

    @POST("api/dashboard/start")
    suspend fun startCharging(): ChargerStatusDto

    @POST("api/dashboard/stop")
    suspend fun stopCharging(): ChargerStatusDto

    @POST("api/dashboard/setChargingParams")
    suspend fun setChargingParams(@Query("current") current: Int): Int
}
