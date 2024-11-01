package sk.momosilabs.suac.server.charging.persistence.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sk.momosilabs.suac.server.charging.persistence.entity.ChargingFinishedEntity

@Repository
interface ChargingFinishedRepository: JpaRepository<ChargingFinishedEntity, Long> {

    fun findAllByAccountId(userId: Long, pageable: Pageable): Page<ChargingFinishedEntity>

    fun countByAccountId(userId: Long): Long

}
