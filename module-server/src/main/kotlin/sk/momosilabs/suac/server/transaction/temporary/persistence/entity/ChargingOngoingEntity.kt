package sk.momosilabs.suac.server.transaction.temporary.persistence.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.validation.constraints.NotNull
import sk.momosilabs.suac.server.account.entity.AccountEntity
import java.time.LocalDateTime

@Entity(name = "charging_ongoing")
class ChargingOngoingEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @field:NotNull
    val trxNumber: Int,

    @field:NotNull
    val trxIdentifier: String,

    @field:NotNull
    val energyMeter: Long,

    @field:NotNull
    val timeStartUtc: LocalDateTime,

    @ManyToOne
    @field:NotNull
    val account: AccountEntity,

)
