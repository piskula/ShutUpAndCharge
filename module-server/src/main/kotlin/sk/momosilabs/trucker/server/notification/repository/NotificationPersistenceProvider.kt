package sk.momosilabs.trucker.server.notification.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sk.momosilabs.trucker.server.notification.model.Notification
import sk.momosilabs.trucker.server.notification.service.NotificationPersistence

@Service
open class NotificationPersistenceProvider(
    private val notificationRepository: NotificationRepository,
): NotificationPersistence {

    @Transactional(readOnly = true)
    override fun list(pageable: Pageable): Page<Notification> =
        notificationRepository.findAll(pageable).toModel()

    @Transactional(readOnly = true)
    override fun listByCompany(companyIds: Set<Long>, pageable: Pageable): Page<Notification> =
        notificationRepository.findAllByCompanyIdIn(companyIds, pageable).toModel()

}
