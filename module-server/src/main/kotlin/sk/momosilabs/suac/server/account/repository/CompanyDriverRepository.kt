package sk.momosilabs.suac.server.account.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sk.momosilabs.suac.server.account.entity.TransportCompanyDriverEntity

@Repository
interface CompanyDriverRepository: JpaRepository<sk.momosilabs.suac.server.account.entity.TransportCompanyDriverEntity, Long> {

    fun findAllByDriverIdKeycloak(userId: String): Iterable<sk.momosilabs.suac.server.account.entity.TransportCompanyDriverEntity>

}
