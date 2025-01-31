package sk.momosilabs.suac.api.publicInfo.dto

import java.time.ZonedDateTime

data class BuildInfoDTO(
    val version: String,
    val name: String,
    val time: ZonedDateTime,
    val url: String,
)
