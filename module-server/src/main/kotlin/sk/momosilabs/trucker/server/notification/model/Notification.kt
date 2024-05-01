package sk.momosilabs.trucker.server.notification.model

import java.time.LocalDate

data class Notification(
    val id: Long,

    val companyId: Long,
    val companyName: String,

    val validUntil: LocalDate?,
)
