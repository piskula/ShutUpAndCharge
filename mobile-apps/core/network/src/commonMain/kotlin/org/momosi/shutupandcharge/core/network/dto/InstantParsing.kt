package org.momosi.shutupandcharge.core.network.dto

import kotlin.time.Instant

private val zoneSuffix = Regex("(Z|[+-]\\d{2}:?\\d{2})$")
private val bracketedZoneId = Regex("\\[[^]]+]$")

fun String.toInstantOrNull(): Instant? {
    val withoutZoneId = replace(bracketedZoneId, "")
    val normalized = if (zoneSuffix.containsMatchIn(withoutZoneId)) withoutZoneId else "${withoutZoneId}Z"
    return runCatching { Instant.parse(normalized) }.getOrNull()
}

fun Instant.toIsoString(): String = toString()
