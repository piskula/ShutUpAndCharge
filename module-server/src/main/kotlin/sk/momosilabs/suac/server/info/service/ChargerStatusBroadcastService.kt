package sk.momosilabs.suac.server.info.service

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import sk.momosilabs.suac.server.dashboard.model.charging.ChargerStatus
import sk.momosilabs.suac.server.dashboard.service.getChargingStatus.GetChargingStatusUseCase
import sk.momosilabs.suac.server.info.controller.mapper.toDto
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

@Service
open class ChargerStatusBroadcastService(
    private val getChargingStatus: GetChargingStatusUseCase,
) {

    companion object {
        private val logger = LoggerFactory.getLogger(ChargerStatusBroadcastService::class.java)
        private val emitterTimeoutMillis = TimeUnit.MINUTES.toMillis(30)
    }

    private val emitters = CopyOnWriteArrayList<SseEmitter>()
    private val lastEventId = AtomicLong(0)

    fun subscribe(): SseEmitter {
        val emitter = SseEmitter(emitterTimeoutMillis)
        emitter.onCompletion { emitters.remove(emitter) }
        emitter.onTimeout { emitters.remove(emitter); emitter.complete() }
        emitter.onError { emitters.remove(emitter) }
        emitters.add(emitter)

        sendTo(emitter, getChargingStatus.getChargerStatus())
        return emitter
    }

    @Scheduled(fixedDelayString = "\${application.station.statusPollIntervalMs:5000}")
    open fun poll() {
        if (emitters.isEmpty()) {
            return
        }

        val status = getChargingStatus.getChargerStatus()
        emitters.forEach { emitter -> sendTo(emitter, status) }
    }

    private fun sendTo(emitter: SseEmitter, status: ChargerStatus) {
        try {
            emitter.send(
                SseEmitter.event()
                    .id(lastEventId.incrementAndGet().toString())
                    .name("status")
                    .data(status.toDto()),
            )
        } catch (e: Exception) {
            logger.debug("Removing dead charger status SSE subscriber", e)
            emitters.remove(emitter)
        }
    }

}
