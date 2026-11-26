package org.momosi.shutupandcharge.core.common.version.dto

import org.momosi.shutupandcharge.core.common.version.model.ServerVersion
import org.momosi.shutupandcharge.dto.BuildInfoDto
import kotlin.time.Instant

fun BuildInfoDto.toServerVersion(): ServerVersion = ServerVersion(
    version = version,
    builtAt = time?.let { runCatching { Instant.parse(it) }.getOrNull() },
)
