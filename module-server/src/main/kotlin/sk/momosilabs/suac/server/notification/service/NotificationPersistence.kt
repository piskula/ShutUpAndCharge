package sk.momosilabs.suac.server.notification.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import sk.momosilabs.suac.server.notification.model.Notification

interface NotificationPersistence {

    fun list(pageable: Pageable): Page<sk.momosilabs.suac.server.notification.model.Notification>
    fun listByCompany(companyIds: Set<Long>, pageable: Pageable): Page<sk.momosilabs.suac.server.notification.model.Notification>

}
