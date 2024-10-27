package sk.momosilabs.suac.server.notification.service.getNotificationList

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import sk.momosilabs.suac.server.notification.model.Notification

interface GetNotificationListUseCase {

    fun listNotifications(pageable: Pageable): Page<sk.momosilabs.suac.server.notification.model.Notification>

}
