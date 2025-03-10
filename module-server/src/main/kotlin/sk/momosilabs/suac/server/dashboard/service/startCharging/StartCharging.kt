package sk.momosilabs.suac.server.dashboard.service.startCharging

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
import java.time.Instant
import java.util.UUID

@Service
open class StartCharging(
    private val currentUserService: CurrentUserService,
    private val accountPersistence: AccountPersistence,
    private val transactionPersistence: TransactionTemporaryPersistence,
    private val externalChargingApi: ExternalChargingApi,
): StartChargingUseCase {

    @IsUser
    @Transactional
    override fun startCharging(): ChargerStatus {
        val canCharge = accountPersistence.canCharge(accountId = currentUserService.userId())
        if (!canCharge)
            throw UserNotAllowedToChargeException()

        val isCableConnected = externalChargingApi.getChargerStatus().ifSuccess?.carState == CarStateEnum.WaitCar
        if (!isCableConnected)
            throw CableNotRecognizedException()

        val trxNumber = (transactionPersistence.getLastNotProcessedTrxNumber()?.plus(1) ?: 5) % 5 + 5

        val identifier = UUID.randomUUID().toString()
        val startingResult = externalChargingApi.startCharging(trxNumber, identifier).ifSuccess ?: unknownStatus
        transactionPersistence.addOngoingTransaction(
            timestamp = Instant.now(),
            accountId = currentUserService.userId(),
            trxNumber = startingResult.trxNumber!!,
            trxIdentifier = startingResult.customIdentifier,
            energyMeter = startingResult.meterEnergyTotal,
        )

        if (startingResult.carState != CarStateEnum.Charging) {
            throw StartChargingFailureException()
        } else {
            return startingResult
        }
    }

}
