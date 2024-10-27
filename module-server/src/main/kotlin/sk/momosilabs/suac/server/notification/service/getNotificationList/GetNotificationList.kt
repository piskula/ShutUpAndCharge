package sk.momosilabs.suac.server.notification.service.getNotificationList

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sk.momosilabs.suac.server.notification.model.Notification
import sk.momosilabs.suac.server.notification.service.NotificationAccountPersistence
import sk.momosilabs.suac.server.notification.service.NotificationPersistence
import sk.momosilabs.suac.server.security.service.CurrentUserService

@Service
open class GetNotificationList(
    private val currentUserService: CurrentUserService,
    private val notificationPersistence: NotificationPersistence,
    private val notificationAccountPersistence: NotificationAccountPersistence,
): GetNotificationListUseCase {

    @Transactional(readOnly = true)
    override fun listNotifications(pageable: Pageable): Page<Notification> {
        val userId = currentUserService.userId()
        val companyIds = notificationAccountPersistence.getAssignedCompanyIds(userId)

        if (companyIds.isNotEmpty()) {
            return notificationPersistence.listByCompany(companyIds, pageable)
        }
        if (notificationAccountPersistence.isAdmin(userId)) {
            return notificationPersistence.list(pageable)
        }

        return Page.empty(pageable)
    }

}
