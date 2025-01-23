package sk.momosilabs.suac.server.dashboard.controller.mapper

import sk.momosilabs.suac.api.dashboard.dto.ChargerStatusDTO
import sk.momosilabs.suac.server.dashboard.model.charging.ChargerStatus
import sk.momosilabs.suac.server.dashboard.model.charging.ChargerStatusEnum
import java.time.ZoneOffset

fun ChargerStatus.toDto() = ChargerStatusDTO(
    ready = status == ChargerStatusEnum.Free,
    offline = status == ChargerStatusEnum.Offline,
    occupied = status == ChargerStatusEnum.Charging,
    occupiedFrom = timestamp?.atOffset(ZoneOffset.UTC),
    chargedKwh = chargedKwh,
    awaitingAuthorization = status == ChargerStatusEnum.AwaitingAuth,
)
