package sk.momosilabs.trucker.server.notification.service.getNotificationList

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import sk.momosilabs.trucker.server.notification.model.Notification

interface GetNotificationListUseCase {

    fun listNotifications(pageable: Pageable): Page<Notification>

}
