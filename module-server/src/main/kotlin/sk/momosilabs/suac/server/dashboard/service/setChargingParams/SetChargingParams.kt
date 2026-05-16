package sk.momosilabs.suac.server.dashboard.service.setChargingParams

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sk.momosilabs.suac.server.account.persistence.AccountPersistence
import sk.momosilabs.suac.server.common.IsUser
import sk.momosilabs.suac.server.dashboard.model.charging.external.CarStateEnum
import sk.momosilabs.suac.server.transaction.temporary.persistence.TransactionTemporaryPersistence
import sk.momosilabs.suac.server.dashboard.service.external.ExternalChargingApi
import sk.momosilabs.suac.server.security.service.CurrentUserService

@Service
open class SetChargingParams(
    private val currentUserService: CurrentUserService,
    private val accountPersistence: AccountPersistence,
    private val transactionPersistence: TransactionTemporaryPersistence,
    private val externalChargingApi: ExternalChargingApi,
): SetChargingParamsUseCase {

    @IsUser
    @Transactional(readOnly = true)
    override fun setChargingParams(current: Int): Int {
        val status = externalChargingApi.getChargerStatus().ifSuccess
        if (status?.carState != CarStateEnum.Charging)
            throw ChargingNotInitiatedException(status?.carState)

        if (currentUserService.isAdmin())
            return setChargingParamsAndReturn(current)

        val userId = currentUserService.keycloakId()
        val canThisUserChangeParams = if (status.rfidUid == null)
            transactionPersistence.isChargingOfUserOngoing(trxIdentifier = status.customIdentifier, accountId = userId)
        else
            accountPersistence.findUserIdByChipUid(status.rfidUid) == userId

        if (!canThisUserChangeParams) {
            throw ChargingDoesNotBelongToUserException()
        }

        return setChargingParamsAndReturn(current)
    }

    private fun setChargingParamsAndReturn(current: Int): Int =
        externalChargingApi.setChargingParams(current)

}
