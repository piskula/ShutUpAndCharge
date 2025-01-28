package sk.momosilabs.suac.server.dashboard.model.charging.external

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import sk.momosilabs.suac.server.dashboard.model.charging.ChargerStatus

data class ExternalChargerDataWrapper(
    val error: Boolean,
    val success: Boolean,
    val ifSuccess: ChargerStatus?,
    val ifError: ExternalChargerDataError?,
)

data class ExternalChargerDataError(
    val lastAliveSecondsAgo: Long,
)

@Serializable
data class ExternalChargerSuccessResponse(
    val frc: ForceStateEnum,
    val car: CarStateEnum,
    val modelStatus: ExternalChargerStatusEnum,
    val tsi: String?,
    val ct: String,
    val trx: Int?,
    val ocppcs: OcppConnectorStatusEnum,
    val wh: Double,
    val etop: Long,
//    val fna: String,
//    val amp: Int,
//    val tma: List<Double>,
//    val lri: String?,
)

@Serializable
data class ExternalChargerErrorResponse(
    val reason: String,
    val age: Double,
)

open class EnumAsIntSerializer<T:Enum<*>>(
    serialName: String,
    val serialize: (v: T) -> Int,
    val deserialize: (v: Int) -> T
) : KSerializer<T> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(serialName, PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeInt(serialize(value))
    }

    override fun deserialize(decoder: Decoder): T {
        val v = decoder.decodeInt()
        return deserialize(v)
    }
}

private class ExternalChargerStatusEnumSerializer: EnumAsIntSerializer<ExternalChargerStatusEnum>(
    "ExternalChargerStatusEnum",
    { it.value },
    { v -> ExternalChargerStatusEnum.entries.first { it.value == v } }
)

@Serializable(with = ExternalChargerStatusEnumSerializer::class)
enum class ExternalChargerStatusEnum(val value: Int) {
    NotChargingBecauseNoChargeCtrlData(0),
    NotChargingBecauseOvertemperature(1),
    NotChargingBecauseAccessControlWait(2),
    ChargingBecauseForceStateOn(3),
    NotChargingBecauseForceStateOff(4),
    NotChargingBecauseScheduler(5),
    NotChargingBecauseEnergyLimit(6),
    ChargingBecauseAwattarPriceLow(7),
    ChargingBecauseAutomaticStopTestLadung(8),
    ChargingBecauseAutomaticStopNotEnoughTime(9),
    ChargingBecauseAutomaticStop(10),
    ChargingBecauseAutomaticStopNoClock(11),
    ChargingBecausePvSurplus(12),
    ChargingBecauseFallbackGoEDefault(13),
    ChargingBecauseFallbackGoEScheduler(14),
    ChargingBecauseFallbackDefault(15),
    NotChargingBecauseFallbackGoEAwattar(16),
    NotChargingBecauseFallbackAwattar(17),
    NotChargingBecauseFallbackAutomaticStop(18),
    ChargingBecauseCarCompatibilityKeepAlive(19),
    ChargingBecauseChargePauseNotAllowed(20),
    NotChargingBecauseSimulateUnplugging(22),
    NotChargingBecausePhaseSwitch(23),
    NotChargingBecauseMinPauseDuration(24),
    NotChargingBecauseError(26),
    NotChargingBecauseLoadManagementDoesntWant(27),
    NotChargingBecauseOcppDoesntWant(28),
    NotChargingBecauseReconnectDelay(29),
    NotChargingBecauseAdapterBlocking(30),
    NotChargingBecauseUnderfrequencyControl(31),
    NotChargingBecauseUnbalancedLoad(32),
    ChargingBecauseDischargingPvBattery(33),
    NotChargingBecauseGridMonitoring(34),
    NotChargingBecauseOcppFallback(35);
}

private class ForceStateEnumSerializer: EnumAsIntSerializer<ForceStateEnum>(
    "ForceStateEnum",
    { it.value },
    { v -> ForceStateEnum.entries.first { it.value == v } }
)

@Serializable(with = ForceStateEnumSerializer::class)
enum class ForceStateEnum(val value: Int) {
    Neutral(0),
    Off(1),
    On(2);
}

private class CarStateEnumSerializer: EnumAsIntSerializer<CarStateEnum>(
    "CarStateEnum",
    { it.value },
    { v -> CarStateEnum.entries.first { it.value == v } }
)

@Serializable(with = CarStateEnumSerializer::class)
enum class CarStateEnum(val value: Int) {
    UnknownOrError(0),
    Idle(1),
    Charging(2),
    WaitCar(3),
    Complete(4),
    Error(5);
}

private class OcppConnectorStatusEnumSerializer: EnumAsIntSerializer<OcppConnectorStatusEnum>(
    "OcppConnectorStatusEnum",
    { it.value },
    { v -> OcppConnectorStatusEnum.entries.first { it.value == v } }
)

@Serializable(with = OcppConnectorStatusEnumSerializer::class)
enum class OcppConnectorStatusEnum(val value: Int) {
    Available(0),
    Preparing(1),
    Charging(2),
    SuspendedEVSE(3),
    SuspendedEV(4),
    Finishing(5),
    Reserved(6),
    Unavailable(7),
    Faulted(8),
}
