package sk.momosilabs.trucker.server.notification.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.validation.constraints.NotNull
import sk.momosilabs.trucker.server.account.entity.TransportCompanyEntity
import java.time.LocalDate

@Entity(name = "notification")
class NotificationEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne
    @field:NotNull
    val company: TransportCompanyEntity,

    val validUntil: LocalDate?,

)
