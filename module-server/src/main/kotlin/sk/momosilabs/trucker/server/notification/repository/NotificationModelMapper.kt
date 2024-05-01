package sk.momosilabs.trucker.server.notification.repository

import org.springframework.data.domain.Page
import sk.momosilabs.trucker.server.notification.entity.NotificationEntity
import sk.momosilabs.trucker.server.notification.model.Notification

fun Page<NotificationEntity>.toModel() = map {
    Notification(
        id = it.id,
        companyId = it.company.id,
        companyName = it.company.name,
        validUntil = it.validUntil,
    )
}
