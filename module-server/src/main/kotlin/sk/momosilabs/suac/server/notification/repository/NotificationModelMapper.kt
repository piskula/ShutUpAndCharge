package sk.momosilabs.suac.server.notification.repository

import org.springframework.data.domain.Page
import sk.momosilabs.suac.server.notification.entity.NotificationEntity
import sk.momosilabs.suac.server.notification.model.Notification

fun Page<sk.momosilabs.suac.server.notification.entity.NotificationEntity>.toModel() = map {
    sk.momosilabs.suac.server.notification.model.Notification(
        id = it.id,
        companyId = it.company.id,
        companyName = it.company.name,
        validUntil = it.validUntil,
    )
}
