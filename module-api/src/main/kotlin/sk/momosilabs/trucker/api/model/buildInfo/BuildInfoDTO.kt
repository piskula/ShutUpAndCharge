package sk.momosilabs.trucker.api.model.buildInfo

import java.time.ZonedDateTime

data class BuildInfoDTO(
    val version: String,
    val name: String,
    val time: ZonedDateTime,
)
