package sk.momosilabs.suac.server.dashboard.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sk.momosilabs.suac.server.dashboard.entity.ChargingOngoingEntity

@Repository
interface ChargingOngoingRepository: JpaRepository<ChargingOngoingEntity, Long> {

    fun findTopTrxNumberByOrderByIdDesc(): ChargingOngoingEntity?

    fun existsByTrxIdentifierAndAccountId(trxIdentifier: String, accountId: Long): Boolean

}
