package org.momosi.shutupandcharge.core.charger.api

import io.ktor.client.HttpClient
import io.ktor.client.plugins.sse.sse
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.serialization.json.Json
import org.momosi.shutupandcharge.dto.ChargerStatusDto

private const val STATUS_EVENT = "status"

internal class ChargerStatusStreamApi(private val sseClient: HttpClient, private val json: Json) {

    fun stream(): Flow<ChargerStatusDto> = channelFlow {
        sseClient.sse(urlString = "info/chargerStatus/stream") {
            incoming.collect { event -> forward(event.event, event.data) }
        }
    }

    private suspend fun ProducerScope<ChargerStatusDto>.forward(eventName: String?, data: String?) {
        if (eventName != STATUS_EVENT || data.isNullOrBlank()) return
        send(json.decodeFromString(ChargerStatusDto.serializer(), data))
    }
}
