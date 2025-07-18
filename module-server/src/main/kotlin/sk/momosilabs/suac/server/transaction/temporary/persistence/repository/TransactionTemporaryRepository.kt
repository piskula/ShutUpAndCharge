package sk.momosilabs.suac.server.transaction.temporary.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sk.momosilabs.suac.server.transaction.temporary.persistence.entity.ChargingOngoingEntity

@Repository
interface TransactionTemporaryRepository: JpaRepository<ChargingOngoingEntity, Long> {

    fun getByTrxIdentifier(trxIdentifier: String): ChargingOngoingEntity?

    fun findTopTrxNumberByOrderByIdDesc(): ChargingOngoingEntity?

    fun existsByTrxIdentifierAndAccountId(trxIdentifier: String, accountId: Long): Boolean

    fun findAllByOrderByIdAsc(): List<ChargingOngoingEntity>

    fun deleteAllByEnergyMeterIn(energyMeterValues: Set<Long>)

}
