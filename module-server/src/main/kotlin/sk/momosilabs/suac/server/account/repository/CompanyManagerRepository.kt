package sk.momosilabs.suac.server.account.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sk.momosilabs.suac.server.account.entity.TransportCompanyManagerEntity

@Repository
interface CompanyManagerRepository: JpaRepository<sk.momosilabs.suac.server.account.entity.TransportCompanyManagerEntity, Long> {

    fun findAllByAdminIdKeycloak(userId: String): Iterable<sk.momosilabs.suac.server.account.entity.TransportCompanyManagerEntity>

}
