package sk.momosilabs.suac.server.dashboard.service.stopCharging

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sk.momosilabs.suac.server.account.persistence.AccountPersistence
import sk.momosilabs.suac.server.common.IsUser
import sk.momosilabs.suac.server.dashboard.model.charging.ChargerStatus
import sk.momosilabs.suac.server.dashboard.model.charging.external.CarStateEnum
import sk.momosilabs.suac.server.transaction.temporary.persistence.TransactionTemporaryPersistence
import sk.momosilabs.suac.server.dashboard.service.external.ExternalChargingApi
import sk.momosilabs.suac.server.dashboard.service.getChargingStatus.GetChargingStatus.Companion.unknownStatus
import sk.momosilabs.suac.server.security.service.CurrentUserService

@Service
open class StopCharging(
    private val externalChargingApi: ExternalChargingApi,
    private val transactionPersistence: TransactionTemporaryPersistence,
    private val accountPersistence: AccountPersistence,
    private val currentUserService: CurrentUserService,
): StopChargingUseCase {

    @IsUser
    @Transactional
    override fun stopCharging(): ChargerStatus {
        val status = externalChargingApi.getChargerStatus().ifSuccess
        if (status?.carState != CarStateEnum.Charging)
            throw ChargingNotOngoingException(status?.carState)

        if (currentUserService.isAdmin())
            return stopChargingAndReturnStatus()

        val userId = currentUserService.userId()
        val canThisUserStop = if (status.rfidUid == null)
            transactionPersistence.isChargingOfUserOngoing(trxIdentifier = status.customIdentifier, accountId = userId)
        else
            accountPersistence.findUserIdByChipUid(status.rfidUid) == userId

        if (!canThisUserStop) {
            throw ChargingDoesNotBelongToUserException()
        }

        return externalChargingApi.stopCharging().ifSuccess ?: unknownStatus
    }

    private fun stopChargingAndReturnStatus() = externalChargingApi.stopCharging().ifSuccess ?: unknownStatus

}
