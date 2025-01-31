package sk.momosilabs.suac.server.info.controller.mapper

import sk.momosilabs.suac.api.publicInfo.dto.CarStateDTO
import sk.momosilabs.suac.api.publicInfo.dto.ChargerStatusDTO
import sk.momosilabs.suac.api.publicInfo.dto.OcppConnectorStatusDTO
import sk.momosilabs.suac.server.dashboard.model.charging.ChargerStatus
import java.time.ZoneOffset

fun ChargerStatus.toDto() = ChargerStatusDTO(
    carState = CarStateDTO.valueOf(carState.name),
    connectorStatusOcpp = OcppConnectorStatusDTO.valueOf(connectorStatusOcpp.name),
    occupiedFrom = occupiedFrom?.atOffset(ZoneOffset.UTC),
    chargedKwh = chargedKwh,
)
