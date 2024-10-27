package sk.momosilabs.suac.server.notification.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sk.momosilabs.suac.server.notification.entity.NotificationEntity

@Repository
interface NotificationRepository: JpaRepository<sk.momosilabs.suac.server.notification.entity.NotificationEntity, Long> {

    fun findAllByCompanyIdIn(companyIds: Set<Long>, pageable: Pageable): Page<sk.momosilabs.suac.server.notification.entity.NotificationEntity>

}
