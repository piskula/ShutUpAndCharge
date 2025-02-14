package sk.momosilabs.suac.server.transaction.temporary.service.enforceSync

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import sk.momosilabs.suac.server.common.IsAdmin
import sk.momosilabs.suac.server.transaction.pairingProcess.service.DownloadChargingReportsEvent

@Service
open class EnforceDownloadAndSync(
    private val eventPublisher: ApplicationEventPublisher,
): EnforceDownloadAndSyncUseCase {

    @IsAdmin
    override fun enforceSync() {
        eventPublisher.publishEvent(DownloadChargingReportsEvent("ETCC:Kutlik:1"))
    }

}
