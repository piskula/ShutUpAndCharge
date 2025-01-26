package sk.momosilabs.suac.server.dashboard.service.startCharging

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sk.momosilabs.suac.server.account.persistence.AccountPersistence
import sk.momosilabs.suac.server.common.IsUser
import sk.momosilabs.suac.server.dashboard.model.charging.ChargerStatus
import sk.momosilabs.suac.server.dashboard.model.charging.external.CarStateEnum
import sk.momosilabs.suac.server.dashboard.service.external.ExternalChargingApi
import sk.momosilabs.suac.server.dashboard.service.getChargingStatus.GetChargingStatus.Companion.unknownStatus
import sk.momosilabs.suac.server.security.service.CurrentUserService

@Service
open class StartCharging(
    private val currentUserService: CurrentUserService,
    private val accountPersistence: AccountPersistence,
    private val externalChargingApi: ExternalChargingApi,
): StartChargingUseCase {

    @IsUser
    @Transactional
    override fun startCharging(): ChargerStatus {
        val canCharge = accountPersistence.canCharge(accountId = currentUserService.userId())
        if (!canCharge)
            throw IllegalArgumentException("user not allowed to charge")

        val isCableConnected = externalChargingApi.getChargerStatus().ifSuccess?.carState == CarStateEnum.WaitCar
        if (!isCableConnected)
            throw IllegalArgumentException("cable needs to be connected to initiate charging")

        return externalChargingApi.startCharging().ifSuccess ?: unknownStatus
    }

}
