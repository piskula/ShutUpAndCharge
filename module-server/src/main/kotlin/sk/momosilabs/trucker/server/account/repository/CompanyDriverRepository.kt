package sk.momosilabs.trucker.server.account.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sk.momosilabs.trucker.server.account.entity.TransportCompanyDriverEntity

@Repository
interface CompanyDriverRepository: JpaRepository<TransportCompanyDriverEntity, Long> {

    fun findAllByDriverIdKeycloak(userId: String): Iterable<TransportCompanyDriverEntity>

}
