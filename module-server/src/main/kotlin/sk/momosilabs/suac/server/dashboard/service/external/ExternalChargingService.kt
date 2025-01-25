package sk.momosilabs.suac.server.dashboard.service.external

import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import sk.momosilabs.suac.server.common.ApplicationProperties
import sk.momosilabs.suac.server.common.ApplicationPropertiesStation
import sk.momosilabs.suac.server.dashboard.model.charging.external.ExternalChargerDataWrapper
import kotlinx.serialization.json.Json
import org.springframework.http.HttpStatus
import sk.momosilabs.suac.server.dashboard.model.charging.ChargerStatus
import sk.momosilabs.suac.server.dashboard.model.charging.external.ExternalChargerDataError
import sk.momosilabs.suac.server.dashboard.model.charging.external.ExternalChargerErrorResponse
import sk.momosilabs.suac.server.dashboard.model.charging.external.ExternalChargerSuccessResponse
import java.math.BigDecimal
import java.math.RoundingMode

@Service
open class ExternalChargingService(
    private val applicationProperties: ApplicationProperties,
): ExternalChargingApi {

    companion object {
        private val logger = LoggerFactory.getLogger(ExternalChargingService::class.java)
        private val deserializer = Json { ignoreUnknownKeys = true }
    }

    override fun getChargerStatus(): ExternalChargerDataWrapper {
        return connectionToStation(applicationProperties.station)
            .exchange { _, response ->
                val isOk = response.statusCode.is2xxSuccessful
                val is404 = response.statusCode === HttpStatus.NOT_FOUND
                ExternalChargerDataWrapper(
                    error = !isOk,
                    success = isOk,
                    ifSuccess = if (isOk) deserializer.decodeFromString<ExternalChargerSuccessResponse>(String(response.body.readAllBytes())).toModel() else null,
                    ifError = if (is404) deserializer.decodeFromString<ExternalChargerErrorResponse>(String(response.body.readAllBytes())).toModel() else null,
                )
            }
    }

    private fun ExternalChargerSuccessResponse.toModel() = ChargerStatus(
        carState = car,
        modelStatus = modelStatus,
        connectorStatusOcpp = ocppcs,
        occupiedFrom = null,
        chargedKwh = BigDecimal.valueOf(wh).divide(BigDecimal.valueOf(1000), 3, RoundingMode.FLOOR),
    )

    private fun ExternalChargerErrorResponse.toModel() = ExternalChargerDataError(
        lastAliveSecondsAgo = age.toLong(),
    )

    private fun connectionToStation(station: ApplicationPropertiesStation) = RestClient.create().get()
        .uri(station.cloudStatusUrl)
        .header(HttpHeaders.AUTHORIZATION, "Bearer ${station.cloudToken}")

}
