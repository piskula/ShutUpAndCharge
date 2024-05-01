package sk.momosilabs.trucker.server.notification.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import sk.momosilabs.trucker.server.notification.model.Notification

interface NotificationPersistence {

    fun list(pageable: Pageable): Page<Notification>
    fun listByCompany(companyIds: Set<Long>, pageable: Pageable): Page<Notification>

}
