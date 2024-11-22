package sk.momosilabs.suac.server.transaction.persistence.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.validation.constraints.NotNull
import sk.momosilabs.suac.server.account.entity.AccountEntity
import java.math.BigDecimal
import java.time.Instant
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
    val time: Instant,

    @field:NotNull
    val kwh: BigDecimal,

    @field:NotNull
    val stationId: String,

    @field:NotNull
    val price: BigDecimal,

)
