package sk.momosilabs.suac.server.transaction.finished.persistence.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.validation.constraints.NotNull
import sk.momosilabs.suac.server.account.entity.AccountEntity
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Entity(name = "charging_finished")
class ChargingFinishedEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @field:NotNull
    val guid: UUID,

    @ManyToOne
    @field:NotNull
    val account: AccountEntity,

    @field:NotNull
    val timeStartUtc: LocalDateTime,

    @field:NotNull
    val kwh: BigDecimal,

    val stationId: String?,

    @field:NotNull
    val price: BigDecimal,

    val stationSession: Long?,

    val energyMeter: Long?,

    val triggeredByChipUid: String?,

    val link: String?,

)
