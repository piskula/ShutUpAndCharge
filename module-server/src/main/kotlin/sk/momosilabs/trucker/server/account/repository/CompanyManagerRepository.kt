package sk.momosilabs.trucker.server.account.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sk.momosilabs.trucker.server.account.entity.TransportCompanyManagerEntity

@Repository
interface CompanyManagerRepository: JpaRepository<TransportCompanyManagerEntity, Long> {

    fun findAllByAdminIdKeycloak(userId: String): Iterable<TransportCompanyManagerEntity>

}
